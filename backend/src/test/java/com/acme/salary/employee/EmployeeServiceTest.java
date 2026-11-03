package com.acme.salary.employee;

import static com.acme.salary.employee.EmployeeFixtures.anEmployee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import com.acme.salary.common.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 1, 15);
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC);

    @Mock
    private EmployeeRepository repository;

    private EmployeeService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeService(repository, FIXED_CLOCK);
    }

    @Test
    void createsEmployeeFromDetails() {
        when(repository.save(any(Employee.class))).then(returnsFirstArg());

        Employee created = service.create(anEmployee().withEmail("new.joiner@acme.example").details());

        assertThat(created).isNotNull();
        assertThat(created.getEmail()).isEqualTo("new.joiner@acme.example");
    }

    @Test
    void rejectsEmailAlreadyInUse() {
        when(repository.existsByEmail("taken@acme.example")).thenReturn(true);

        assertThatThrownBy(() -> service.create(anEmployee().withEmail("taken@acme.example").details()))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("taken@acme.example");
        verify(repository, never()).save(any());
    }

    @Test
    void rejectsHireDateInTheFuture() {
        EmployeeDetails startsTomorrow = anEmployee().withHireDate(TODAY.plusDays(1)).details();

        assertThatThrownBy(() -> service.create(startsTomorrow))
                .isInstanceOf(InvalidEmployeeException.class)
                .hasMessageContaining("future");
        verify(repository, never()).save(any());
    }

    @Test
    void acceptsHireDateOfToday() {
        when(repository.save(any(Employee.class))).then(returnsFirstArg());

        Employee created = service.create(anEmployee().withHireDate(TODAY).details());

        assertThat(created.getHireDate()).isEqualTo(TODAY);
    }

    @Test
    void findsEmployeeById() {
        Employee existing = new Employee(anEmployee().details());
        when(repository.findById(7L)).thenReturn(Optional.of(existing));

        assertThat(service.get(7L)).isSameAs(existing);
    }

    @Test
    void reportsUnknownEmployee() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(99L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updatesExistingEmployee() {
        Employee existing = new Employee(anEmployee().withCountry(Country.INDIA).details());
        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        EmployeeDetails relocated = anEmployee()
                .withEmail("asha.rao@acme.de")
                .withCountry(Country.GERMANY)
                .details();

        Employee updated = service.update(7L, relocated);

        assertThat(updated).isNotNull();
        assertThat(updated.getEmail()).isEqualTo("asha.rao@acme.de");
        assertThat(updated.getCountry()).isEqualTo(Country.GERMANY);
    }

    @Test
    void reportsUnknownEmployeeOnUpdate() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, anEmployee().details()))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void rejectsEmailUsedByAnotherEmployeeOnUpdate() {
        when(repository.findById(7L)).thenReturn(Optional.of(new Employee(anEmployee().details())));
        when(repository.existsByEmailAndIdNot("taken@acme.example", 7L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(7L, anEmployee().withEmail("taken@acme.example").details()))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void rejectsFutureHireDateOnUpdate() {
        when(repository.findById(7L)).thenReturn(Optional.of(new Employee(anEmployee().details())));

        assertThatThrownBy(() -> service.update(7L, anEmployee().withHireDate(TODAY.plusDays(1)).details()))
                .isInstanceOf(InvalidEmployeeException.class);
    }
}
