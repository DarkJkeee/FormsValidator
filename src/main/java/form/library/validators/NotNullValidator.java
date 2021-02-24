package form.library.validators;

import form.library.interfaces.ValidationError;
import form.library.services.ServiceError;

import java.lang.annotation.Annotation;

class NotNullValidator implements FormValidator {
    private static FormValidator instance;
    private NotNullValidator() { }

    @Override
    public ValidationError validateValue(Object fieldVal, Annotation annotation, String path) {
        ServiceError error = null;
        if (fieldVal == null)
            error = new ServiceError(path, "Must not be null", null);
        return error;
    }

    public static FormValidator getValidator() {
        if (instance == null)
            instance = new NotNullValidator();
        return instance;
    }
}
