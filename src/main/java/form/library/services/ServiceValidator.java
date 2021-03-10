package form.library.services;

import form.library.annotations.*;
import form.library.interfaces.ValidationError;
import form.library.interfaces.Validator;
import form.library.validators.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.*;

/**
 * The validator that simply checks some fields
 * and types using annotations and ReflectionAPI.
 */
public class ServiceValidator implements Validator {

    private final Map<Class<? extends Annotation>, FormValidator> annotations;       // Map of annotation types and their validators.
    private static ServiceValidator instance;                                        // private static instance for singleton.
    private ServiceValidator() {
        annotations = new Validators().getAnnotations();
    }

    public static ServiceValidator getInstance() {
        if (instance == null)
            instance = new ServiceValidator();
        return instance;
    }

    /**
     * The main method which validate specified object.
     * @param object object which should have been validated.
     * @return all errors in the object.
     */
    @Override
    public Set<ValidationError> validate(Object object) {
        if (object != null)
            return validateFields(object, "");
        return new LinkedHashSet<>();
    }

    /**
     * Validates specified fields in object.
     * @param object object to check.
     * @param path path to the field.
     * @return set of errors in object.
     */
    private Set<ValidationError> validateFields(Object object, String path) {
        var errors = new LinkedHashSet<ValidationError>();
        if (object.getClass().isAnnotationPresent(Constrained.class)) {
            Arrays.stream(object.getClass().getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .forEach(field -> {
                    var customObject = getValue(object, field);

                    errors.addAll(validateField(customObject, field.getAnnotatedType(), path + field.getName()));

                    if (customObject != null) {
                        errors.addAll(validateFields(customObject, path + field.getName() + "."));
                        if (field.getType().equals(List.class))
                            errors.addAll(validateList((List<?>)customObject, field.getAnnotatedType(), path + field.getName()));
                    }
                });
        }
        return errors;
    }

    /**
     * Validates specified annotations in field.
     * @param object object which has this field.
     * @param annotatedType annotated type of all annotations.
     * @return set of errors in field.
     */
    private Set<ValidationError> validateField(Object object, AnnotatedType annotatedType, String path) {
        var errors = new LinkedHashSet<ValidationError>();

        Arrays.stream(annotatedType.getAnnotations()).forEach(annotation -> {
            var validator = this.annotations.get(annotation.annotationType());
            if (validator != null) {
                var error = validator.validateValue(object, annotation, path);
                if (error != null)
                    errors.add(error);
            }
        });

        return errors;
    }

    /**
     * Validates list.
     * @param values list.
     * @param annotationType annotation.
     * @param path path to the element or list in recursion .
     * @return set of errors in list.
     */
    private Set<ValidationError> validateList(List<?> values, AnnotatedType annotationType, String path) {
        var errors = new LinkedHashSet<ValidationError>();
            for (int i = 0; i < values.size(); ++i) {
                var element = values.get(i);

                if (element != null) {
                    if (element.getClass().isAnnotationPresent(Constrained.class))
                        errors.addAll(validateFields(element, String.format("%s[%d].", path, i)));

                    AnnotatedType annotatedType = null;
                    if (annotationType instanceof AnnotatedParameterizedType)
                        annotatedType = ((AnnotatedParameterizedType) annotationType).getAnnotatedActualTypeArguments()[0];

                    if (annotatedType != null) {
                        errors.addAll(validateField(element, annotatedType, String.format("%s[%d]", path, i)));

                        if (element instanceof List)
                            errors.addAll(validateList((List<?>)element, annotatedType, String.format("%s[%d]", path, i)));
                    }
                }
            }
        return errors;
    }

    /**
     * Takes value of field in object.
     * @param object object which consist this field.
     * @param field field in the object.
     * @return value.
     */
    private Object getValue(Object object, Field field) {
        try {
            return field.get(object);
        } catch (IllegalAccessException ex) {
            System.out.printf("There are no such field: %s in the object %s", field.getName(), object.getClass().getSimpleName());
        }
        return null;
    }
}