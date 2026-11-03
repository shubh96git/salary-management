package com.acme.salary.employee;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salary.common.Country;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

/**
 * Runs against the real MySQL schema created by Flyway; each test rolls back.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void storesAndReloadsAnEmployee() {
        Employee saved = repository.save(new Employee(new EmployeeDetails(
                "Asha Rao",
                "asha.rao@acme.example",
                "Software Engineer",
                "Engineering",
                Country.INDIA,
                new BigDecimal("1500000.00"),
                LocalDate.of(2021, 4, 1))));
        entityManager.flush();
        entityManager.clear();

        Employee reloaded = repository.findById(saved.getId()).orElseThrow();

        assertThat(reloaded.getCountry()).isEqualTo(Country.INDIA);
        assertThat(reloaded.getCurrency()).isEqualTo(Currency.getInstance("INR"));
        assertThat(reloaded.getAnnualSalary()).isEqualByComparingTo("1500000.00");
        assertThat(reloaded.getHireDate()).isEqualTo(LocalDate.of(2021, 4, 1));
    }
}
