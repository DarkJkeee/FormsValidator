package form.library.validators;

import form.library.interfaces.ValidationError;
import form.library.services.ServiceError;

import java.lang.annotation.Annotation;

public class NotBlankValidator implements FormValidator {
    private static FormValidator instance;
    private NotBlankValidator() { }

    @Override
    public ValidationError validateValue(Object fieldVal, Annotation annotation, String path) {
        ServiceError error = null;

        if (fieldVal != null) {
            try {
                var value = (String)fieldVal;
                if (value.isBlank())
                    error = new ServiceError(path, "Must not be blank!", fieldVal);
            } catch (ClassCastException ex) {
                System.out.printf("NotBlank annotation can be only with String type. Problem with: %s", path);
            }
        }

        return error;
    }

    public static FormValidator getValidator() {
        if (instance == null)
            instance = new NotBlankValidator();
        return instance;
    }
}
