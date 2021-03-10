package form.library.services;

import form.library.annotations.*;
import form.library.interfaces.ValidationError;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class ServiceValidatorTest {
    boolean equalsErrors(Set<ValidationError> errors1, Set<ValidationError> errors2) {
        var expectedErrors = new ArrayList<>(errors1);
        var actualErrors = new ArrayList<>(errors2);
        if (errors1.size() != errors2.size())
            return false;
        for (int i = 0; i < errors1.size(); ++i) {
            if (expectedErrors.get(i).getFailedValue() != null && actualErrors.get(i).getFailedValue() != null) {
                if (!expectedErrors.get(i).getFailedValue().equals(actualErrors.get(i).getFailedValue()))
                    return false;
            }
            if (!expectedErrors.get(i).getPath().equals(actualErrors.get(i).getPath()))
                return false;
            if (!expectedErrors.get(i).getMessage().equals(actualErrors.get(i).getMessage()))
                return false;
        }
        return true;
    }

    @Constrained
    class Person {
        @NotNull
        @NotBlank
        private String firstName;

        @NotBlank
        @NotNull
        private String lastName;

        @InRange(min = 0, max = 150)
        private int age;

        @AnyOf({"Pfizer", "Sputnik", "CoronaVac"})
        private String vaccine;

        public Person(String firstName, String lastName, int age, String vaccine) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.vaccine = vaccine;
        }
    }

    @Constrained
    class VaccineForm {
        @NotNull
        @Size(min = 1, max = 3)
        private List<@Size(min = 1, max = 10) List<@NotNull Person>> members;

        public VaccineForm(List<List<Person>> members) {
            this.members = members;
        }
    }

    @Test
    void validate() {
        var validator = ServiceValidator.getInstance();

        var juniors = List.of(
                new Person(null,"Know",21, "Pfizer"),
                new Person("","Pfizer",-3, "CoronaVac")
        );

        var adults = List.of(
                new Person("John","Snow",40, "Sputnik"),
                new Person("","Pfizer",49, "CoronaVac")
        );

        var form = new VaccineForm(List.of(juniors, adults));

        var errors = validator.validate(form);


        Set<ValidationError> customErrors = new LinkedHashSet<>();
        customErrors.add(new ServiceError("members[0][0].firstName", "Must not be null", null));
        customErrors.add(new ServiceError("members[0][1].firstName", "Must not be blank!", ""));
        customErrors.add(new ServiceError("members[0][1].age", "Must be in range between 0 and 150", -3));
        customErrors.add(new ServiceError("members[1][1].firstName", "Must not be blank!", ""));

        assertTrue(equalsErrors(errors, customErrors));
    }

    @Test
    void checkNull() {
        ServiceValidator.getInstance().validate(null);
    }
}