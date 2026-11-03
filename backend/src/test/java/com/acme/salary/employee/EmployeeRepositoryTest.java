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

    @Test
    void listsDistinctJobTitlesAndDepartmentsAlphabetically() {
        repository.save(new Employee(anEmployee().withEmail("a@acme.example")
                .withJobTitle("Software Engineer").withDepartment("Engineering").details()));
        repository.save(new Employee(anEmployee().withEmail("b@acme.example")
                .withJobTitle("Product Manager").withDepartment("Product").details()));
        repository.save(new Employee(anEmployee().withEmail("c@acme.example")
                .withJobTitle("Software Engineer").withDepartment("Engineering").details()));

        assertThat(repository.findDistinctJobTitles()).containsExactly("Product Manager", "Software Engineer");
        assertThat(repository.findDistinctDepartments()).containsExactly("Engineering", "Product");
    }
}
