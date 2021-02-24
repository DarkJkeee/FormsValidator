package form.library.validators;

import form.library.annotations.AnyOf;
import form.library.interfaces.ValidationError;
import form.library.services.ServiceError;

import java.lang.annotation.Annotation;
import java.util.Arrays;

class AnyOfValidator implements FormValidator {
    private static FormValidator instance;
    private AnyOfValidator() { }

    @Override
    public ValidationError validateValue(Object fieldVal, Annotation annotation, String path) {
        ServiceError error = null;
        var values = ((AnyOf) annotation).value();

        if (fieldVal != null) {
            try {
                var value = (String) fieldVal;
                if (!Arrays.asList(values).contains(value))
                    error = new ServiceError(path, String.format("Must be one of %s", Arrays.toString(values)), fieldVal);

            } catch (ClassCastException ex) {
                System.out.printf("AnyOf annotation can be only with String type. Problem with: %s", path);
            }
        }
        return error;
    }

    public static FormValidator getValidator() {
        if (instance == null)
            instance = new AnyOfValidator();
        return instance;
    }
}
