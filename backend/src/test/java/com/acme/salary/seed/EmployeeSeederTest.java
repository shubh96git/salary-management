package com.acme.salary.seed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.acme.salary.employee.Employee;
import com.acme.salary.employee.EmployeeDetails;
import com.acme.salary.employee.EmployeeRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

/**
 * Runs against the real MySQL schema created by Flyway; each test rolls back.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(EmployeeSeeder.class)
class EmployeeSeederTest {

    private final List<EmployeeDetails> generated = new EmployeeGenerator(7L).generate(3);

    @Autowired
    private EmployeeSeeder seeder;

    @Autowired
    private EmployeeRepository repository;

    @Test
    void insertsEveryEmployeeIntoAnEmptyDatabase() {
        int inserted = seeder.seedIfEmpty(() -> generated);

        assertThat(inserted).isEqualTo(3);
        assertThat(repository.findAll())
                .extracting(Employee::getEmail, Employee::getCountry, Employee::getCurrency)
                .containsExactlyInAnyOrderElementsOf(generated.stream()
                        .map(Employee::new)
                        .map(employee -> tuple(employee.getEmail(), employee.getCountry(), employee.getCurrency()))
                        .toList());
    }

    @Test
    void leavesAnExistingDatabaseUntouched() {
        repository.saveAndFlush(new Employee(generated.getFirst()));

        int inserted = seeder.seedIfEmpty(() -> generated);

        assertThat(inserted).isZero();
        assertThat(repository.count()).isEqualTo(1);
    }
}
