package form.library.validators;

import form.library.interfaces.ValidationError;

import java.lang.annotation.Annotation;

public interface FormValidator {
    /**
     * Validates value with annotation rules.
     * @param fieldVal value which should be checked.
     * @param annotation annotaiton.
     * @param path path to the field of this value.
     * @return error if value doesn't appropriate to conditions.
     */
    ValidationError validateValue(Object fieldVal, Annotation annotation, String path);
}
