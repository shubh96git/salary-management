package com.acme.salary.employee;

import static com.acme.salary.employee.EmployeeFixtures.anEmployee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    private EmployeeService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeService(repository);
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
}
