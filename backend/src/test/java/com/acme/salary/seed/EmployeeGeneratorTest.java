package com.acme.salary.seed;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salary.common.Country;
import com.acme.salary.employee.EmployeeDetails;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EmployeeGeneratorTest {

    private static final int ORG_SIZE = 10_000;

    private static List<EmployeeDetails> employees;

    @BeforeAll
    static void generateOnce() {
        employees = new EmployeeGenerator(42L).generate(ORG_SIZE);
    }

    @Test
    void generatesTheRequestedNumberOfEmployees() {
        assertThat(employees).hasSize(ORG_SIZE);
    }

    @Test
    void generatesTheSameEmployeesForTheSameSeed() {
        assertThat(new EmployeeGenerator(42L).generate(ORG_SIZE)).isEqualTo(employees);
    }

    @Test
    void givesEveryEmployeeAUniqueEmail() {
        assertThat(employees).extracting(EmployeeDetails::email).doesNotHaveDuplicates();
    }

    @Test
    void generatesOnlyDataTheApiWouldAccept() {
        LocalDate latestHireDate = LocalDate.of(2025, 12, 31);

        assertThat(employees).allSatisfy(employee -> {
            assertThat(employee.fullName()).isNotBlank().hasSizeLessThanOrEqualTo(100);
            assertThat(employee.email()).matches("[a-z0-9.]+@acme\\.example");
            assertThat(employee.jobTitle()).isNotBlank().hasSizeLessThanOrEqualTo(100);
            assertThat(employee.department()).isNotBlank().hasSizeLessThanOrEqualTo(100);
            assertThat(employee.country()).isNotNull();
            assertThat(employee.annualSalary()).isPositive();
            assertThat(employee.annualSalary().scale()).isEqualTo(2);
            assertThat(employee.hireDate()).isBeforeOrEqualTo(latestHireDate);
        });
    }

    @Test
    void employsPeopleInEverySupportedCountry() {
        assertThat(employees).extracting(EmployeeDetails::country).contains(Country.values());
    }

    @Test
    void paysSeniorEngineersMoreThanEngineersInEveryCountry() {
        Map<Country, Map<String, Double>> averageByCountryAndTitle = employees.stream()
                .collect(groupingBy(EmployeeDetails::country,
                        groupingBy(EmployeeDetails::jobTitle,
                                averagingDouble(employee -> employee.annualSalary().doubleValue()))));

        assertThat(averageByCountryAndTitle).hasSize(Country.values().length);
        averageByCountryAndTitle.forEach((country, averageByTitle) ->
                assertThat(averageByTitle.get("Senior Software Engineer"))
                        .as("senior vs. engineer pay in %s", country)
                        .isGreaterThan(averageByTitle.get("Software Engineer")));
    }
}
