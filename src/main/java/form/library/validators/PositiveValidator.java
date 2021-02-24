package form.library.validators;

import form.library.interfaces.ValidationError;
import form.library.services.ServiceError;

import java.lang.annotation.Annotation;

class PositiveValidator implements FormValidator {
    private static FormValidator instance;
    private PositiveValidator() { }


    @Override
    public ValidationError validateValue(Object fieldVal, Annotation annotation, String path) {
        ServiceError error = null;

        if (fieldVal != null) {
            try {
                var value = ((Number)fieldVal).longValue();
                if (value <= 0)
                    error = new ServiceError(path, "Must be positive!", fieldVal);
            } catch (ClassCastException ex) {
                System.out.printf("Positive annotation can be only with numbers. Problem with: %s", path);
            }
        }

        return error;
    }

    public static FormValidator getValidator() {
        if (instance == null)
            instance = new PositiveValidator();
        return instance;
    }
}
