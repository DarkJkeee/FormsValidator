package form.library.validators;

import form.library.interfaces.ValidationError;
import form.library.services.ServiceError;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

class NotEmptyValidator implements FormValidator{
    private static FormValidator instance;
    private NotEmptyValidator() { }

    @Override
    public ValidationError validateValue(Object fieldVal, Annotation annotation, String path) {
        ServiceError error = null;

        if (fieldVal != null) {
            try {
                if (fieldVal instanceof String) {
                    var value = (String)fieldVal;
                    if (value.isEmpty())
                        error = new ServiceError(path, "String shouldn't be empty", fieldVal);

                }
                else if (fieldVal instanceof Map<?, ?>) {
                    var value = (Map<?, ?>)fieldVal;
                    if (value.isEmpty())
                        error = new ServiceError(path, "Map shouldn't be empty", fieldVal);
                }
                else {
                    var value = (Collection<?>)fieldVal;
                    if (value.isEmpty())
                        error = new ServiceError(path, "Collection shouldn't be empty", fieldVal);
                }
            } catch (ClassCastException ex) {
                System.out.printf("NotEmpty annotation can be only with collections, maps or string. Problem with: %s", path);
            }
        }

        return error;
    }

    public static FormValidator getValidator() {
        if (instance == null)
            instance = new NotEmptyValidator();
        return instance;
    }
}
