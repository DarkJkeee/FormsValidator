package form.library.validators;

import form.library.annotations.*;
import form.library.interfaces.ValidationError;
import form.library.services.ServiceError;
import form.library.services.ServiceValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

class FormValidatorTest {
    private static ServiceValidator validator;

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @BeforeAll
    static void beforeAll() {
        validator = ServiceValidator.getInstance();
    }

    boolean equalsErrors(Set<ValidationError> errors1, Set<ValidationError> errors2) {
        var expectedErrors = new ArrayList<>(errors1);
        var actualErrors = new ArrayList<>(errors2);
        if (errors1.size() != errors2.size())
            return false;
        for (int i = 0; i < errors1.size(); ++i) {
            if (!expectedErrors.get(i).getFailedValue().equals(actualErrors.get(i).getFailedValue()))
                return false;
            if (!expectedErrors.get(i).getPath().equals(actualErrors.get(i).getPath()))
                return false;
            if(!expectedErrors.get(i).getMessage().equals(actualErrors.get(i).getMessage()))
                return false;
        }
        return true;
    }

    @Test
    void anyOfTest() {

        @Constrained
        class TestAnyOf {
            @AnyOf({"Hello", "hi", ","})
            private final String invalidString = "Hello, Mr.Green!";

            @AnyOf({"Hello", "hi", ","})
            private final String validString = "Hello";

            @AnyOf({"Hello", "hi", ","})
            private final int exception = 5;
        }

        var anyOf = new TestAnyOf();

        var errors = validator.validate(anyOf);
        var validErrors = new LinkedHashSet<ValidationError>();
        validErrors.add(new ServiceError("invalidString", "Must be one of [Hello, hi, ,]", anyOf.invalidString));


        assertTrue(equalsErrors(errors, validErrors));
        assertEquals("AnyOf annotation can be only with String type. Problem with: exception", outputStreamCaptor.toString().trim());
    }

    @Test
    void inRangeTest() {
        @Constrained
        class TestInRange {
            @InRange(min = 17, max = 200)
            private final int invalidValue = 5;

            @InRange(min = 9, max = 11)
            private final byte validValue = 10;
            @InRange(min = 9, max = 11)
            private final long validValueLong = 10L;
            @InRange(min = 9, max = 11)
            private final Integer validValueInteger = 10;

            @InRange(min = 0, max = 1)
            private final String exception = "eagle";
        }

        var inRange = new TestInRange();

        var errors = validator.validate(inRange);
        var validErrors = new LinkedHashSet<ValidationError>();
        validErrors.add(new ServiceError("invalidValue", "Must be in range between 17 and 200", inRange.invalidValue));

        assertTrue(equalsErrors(errors, validErrors));
        assertEquals("InRange annotation can be only with numbers. Problem with: exception", outputStreamCaptor.toString().trim());
    }

    @Test
    void negativeTest() {
        @Constrained
        class TestNegative {
            @Negative
            private final Long invalidValue = 5L;

            @Negative
            private final int validValue = -5;
            @Negative
            private final long validValueLong = -10L;
            @Negative
            private final short validValueShort = -5;

            @Negative
            private final String exception = "eagle";
        }

        var negative = new TestNegative();

        var errors = validator.validate(negative);
        var validErrors = new LinkedHashSet<ValidationError>();
        validErrors.add(new ServiceError("invalidValue", "Must be negative!", negative.invalidValue));

        assertTrue(equalsErrors(errors, validErrors));
        assertEquals("Negative annotation can be only with numbers. Problem with: exception", outputStreamCaptor.toString().trim());

    }

    @Test
    void positiveTest() {
        @Constrained
        class TestPositive {
            @Positive
            private final Long invalidValue = -5L;

            @Positive
            private final int validValue = 5;
            @Positive
            private final long validValueLong = 10L;
            @Positive
            private final short validValueShort = 5;

            @Positive
            private final String exception = "eagle";
        }

        var positive = new TestPositive();

        var errors = validator.validate(positive);
        var validErrors = new LinkedHashSet<ValidationError>();
        validErrors.add(new ServiceError("invalidValue", "Must be positive!", positive.invalidValue));

        assertTrue(equalsErrors(errors, validErrors));
        assertEquals("Positive annotation can be only with numbers. Problem with: exception", outputStreamCaptor.toString().trim());
    }

    @Test
    void notBlankTest() {
        @Constrained
        class TestNotBlank {
            @NotBlank
            private final String invalidValue = "  ";

            @NotBlank
            private final String validValue = "Hi!!";

            @NotBlank
            private final Long exception = 5L;
        }

        var notBlank = new TestNotBlank();

        var errors = validator.validate(notBlank);
        var validErrors = new LinkedHashSet<ValidationError>();
        validErrors.add(new ServiceError("invalidValue", "Must not be blank!", notBlank.invalidValue));

        assertTrue(equalsErrors(errors, validErrors));
        assertEquals("NotBlank annotation can be only with String type. Problem with: exception", outputStreamCaptor.toString().trim());
    }

    @Test
    void notEmptyTest() {
        @Constrained
        class TestNotEmpty {
            @NotEmpty
            private final String invalidValue = "";
            @NotEmpty
            private final Set<Integer> invalidCollection = new HashSet<>();
            @NotEmpty
            private final Map<Integer, Boolean> invalidMap = new HashMap<>();

            @NotEmpty
            private final String validValue = "Hi!!";

            @NotEmpty
            private final Long exception = 5L;
        }

        var notEmpty = new TestNotEmpty();

        var errors = validator.validate(notEmpty);
        var validErrors = new LinkedHashSet<ValidationError>();
        validErrors.add(new ServiceError("invalidValue", "String shouldn't be empty", notEmpty.invalidValue));
        validErrors.add(new ServiceError("invalidCollection", "Collection shouldn't be empty", notEmpty.invalidCollection));
        validErrors.add(new ServiceError("invalidMap", "Map shouldn't be empty", notEmpty.invalidMap));

        assertTrue(equalsErrors(errors, validErrors));
        assertEquals("NotEmpty annotation can be only with collections, maps or string. Problem with: exception", outputStreamCaptor.toString().trim());
    }

    @Test
    void sizeTest() {
        @Constrained
        class TestNotEmpty {
            @Size(min = 1, max = 3)
            private final String invalidValue = "Hello!";
            @Size(min = 1, max = 3)
            private final HashSet<Integer> invalidCollection = new HashSet<>();
            @Size(min = 1, max = 5)
            private final Map<Integer, Boolean> invalidMap = new HashMap<>();

            @Size(min = 1, max = 4)
            private final List<Integer> validValue = List.of(1, 2, 3);

            @Size(min = 1, max = 5)
            private final Long exception = 5L;
        }

        var notEmpty = new TestNotEmpty();

        var errors = validator.validate(notEmpty);
        var validErrors = new LinkedHashSet<ValidationError>();
        validErrors.add(new ServiceError("invalidValue", "String length should be between 1 and 3", notEmpty.invalidValue));
        validErrors.add(new ServiceError("invalidCollection", "Collection size should be between 1 and 3", notEmpty.invalidCollection));
        validErrors.add(new ServiceError("invalidMap", "Map size should be between 1 and 5", notEmpty.invalidMap));

        assertTrue(equalsErrors(errors, validErrors));
        assertEquals("Size annotation can be only with collections, maps or strings. Problem with: exception", outputStreamCaptor.toString().trim());
    }

    @Test
    void notNullTest() {
        @Constrained
        class TestNotNull {
            @NotNull
            private final String invalidValue = null;

            @NotNull
            private final String validValue = "";
        }

        var notNull = new TestNotNull();

        var errors = validator.validate(notNull);
        var validErrors = new LinkedHashSet<ValidationError>();
        validErrors.add(new ServiceError("invalidValue", "Must not be null", null));

        assertEquals(new ArrayList<>(errors).get(0).getFailedValue(), new ArrayList<>(validErrors).get(0).getFailedValue());
        assertEquals(new ArrayList<>(errors).get(0).getMessage(), new ArrayList<>(validErrors).get(0).getMessage());
        assertEquals(new ArrayList<>(errors).get(0).getPath(), new ArrayList<>(validErrors).get(0).getPath());
    }
}