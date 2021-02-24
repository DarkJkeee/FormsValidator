package form.library.validators;

import form.library.annotations.*;

import java.lang.annotation.Annotation;
import java.util.Map;

public class Validators {
    private final Map<Class<? extends Annotation>, FormValidator> annotations;

    public Validators() {
        annotations = Map.of(
                AnyOf.class, AnyOfValidator.getValidator(),
                InRange.class, InRangeValidator.getValidator(),
                Negative.class, NegativeValidator.getValidator(),
                NotBlank.class, NotBlankValidator.getValidator(),
                NotEmpty.class, NotEmptyValidator.getValidator(),
                NotNull.class, NotNullValidator.getValidator(),
                Positive.class, PositiveValidator.getValidator(),
                Size.class, SizeValidator.getValidator()
        );
    }

    public Map<Class<? extends Annotation>, FormValidator> getAnnotations() {
        return annotations;
    }
}
