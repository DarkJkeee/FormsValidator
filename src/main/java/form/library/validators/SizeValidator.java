package form.library.validators;

import form.library.annotations.Size;
import form.library.interfaces.ValidationError;
import form.library.services.ServiceError;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

class SizeValidator implements FormValidator {
    private static FormValidator instance;
    private SizeValidator() { }

    @Override
    public ValidationError validateValue(Object fieldVal, Annotation annotation, String path) {
        ServiceError error = null;
        int min = ((Size)annotation).min();
        int max = ((Size)annotation).max();

        if (fieldVal != null) {
            try {
                if (fieldVal instanceof String) {
                    var value = (String)fieldVal;
                    if (!(value.length() >= min && value.length() <= max))
                        error = new ServiceError(path, String.format("String length should be between %d and %d", min, max), fieldVal);
                }
                else if (fieldVal instanceof Map<?, ?>) {
                    var value = (Map<?, ?>)fieldVal;
                    if (!(value.size() >= min && value.size() <= max))
                        error = new ServiceError(path, String.format("Map size should be between %d and %d", min, max), fieldVal);
                }
                else {
                    var value = (Collection<?>)fieldVal;
                    if (!(value.size() >= min && value.size() <= max))
                        error = new ServiceError(path, String.format("Collection size should be between %d and %d", min, max), fieldVal);
                }
            } catch (ClassCastException ex) {
                System.out.printf("Size annotation can be only with collections, maps or strings. Problem with: %s", path);
            }
        }

        return error;
    }

    public static FormValidator getValidator() {
        if (instance == null)
            instance = new SizeValidator();
        return instance;
    }
}
