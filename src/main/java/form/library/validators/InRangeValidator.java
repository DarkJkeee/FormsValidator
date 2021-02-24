package form.library.validators;

import form.library.annotations.InRange;
import form.library.interfaces.ValidationError;
import form.library.services.ServiceError;

import java.lang.annotation.Annotation;

class InRangeValidator implements FormValidator {
    private static FormValidator instance;
    private InRangeValidator() { }

    @Override
    public ValidationError validateValue(Object fieldVal, Annotation annotation, String path) {
        ServiceError error = null;
        long min = ((InRange)annotation).min();
        long max = ((InRange)annotation).max();

        if (fieldVal != null) {
            try {
                var value = (Number) fieldVal;
                if (!(value.longValue() >= min && value.longValue() <= max))
                    error = new ServiceError(path, String.format("Must be in range between %d and %d", min, max), fieldVal);
            } catch (ClassCastException ex) {
                System.out.printf("InRange annotation can be only with numbers. Problem with: %s", path);
            }
        }
        return error;
    }

    public static FormValidator getValidator() {
        if (instance == null)
            instance = new InRangeValidator();
        return instance;
    }
}
