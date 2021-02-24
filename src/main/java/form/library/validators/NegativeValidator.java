package form.library.validators;

import form.library.interfaces.ValidationError;
import form.library.services.ServiceError;

import java.lang.annotation.Annotation;

class NegativeValidator implements FormValidator {
    private static FormValidator instance;
    private NegativeValidator() { }

    @Override
    public ValidationError validateValue(Object fieldVal, Annotation annotation, String path) {
        ServiceError error = null;

        if (fieldVal != null) {
            try {
                var value = ((Number)fieldVal).longValue();
                if (value >= 0)
                    error = new ServiceError(path, "Must be negative!", fieldVal);
            } catch (ClassCastException ex) {
                System.out.printf("Negative annotation can be only with numbers. Problem with: %s", path);
            }
        }
        return error;
    }

    public static FormValidator getValidator() {
        if (instance == null)
            instance = new NegativeValidator();
        return instance;
    }
}
