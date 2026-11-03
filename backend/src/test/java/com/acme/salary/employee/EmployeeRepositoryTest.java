package com.acme.salary.employee;

import static com.acme.salary.employee.EmployeeFixtures.anEmployee;
import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salary.common.Country;
import com.acme.salary.support.DatabaseTest;
import jakarta.persistence.EntityManager;
import java.util.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DatabaseTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void storesAndReloadsAnEmployee() {
        EmployeeDetails details = anEmployee().withCountry(Country.INDIA).details();
        Employee saved = repository.save(new Employee(details));
        entityManager.flush();
        entityManager.clear();

        Employee reloaded = repository.findById(saved.getId()).orElseThrow();

        assertThat(reloaded.getCountry()).isEqualTo(Country.INDIA);
        assertThat(reloaded.getCurrency()).isEqualTo(Currency.getInstance("INR"));
        assertThat(reloaded.getAnnualSalary()).isEqualByComparingTo(details.annualSalary());
        assertThat(reloaded.getHireDate()).isEqualTo(details.hireDate());
    }
}
