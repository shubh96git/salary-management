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
}
