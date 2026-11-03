package com.acme.salary.insights;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salary.common.Country;
import com.acme.salary.employee.Employee;
import com.acme.salary.employee.EmployeeDetails;
import com.acme.salary.employee.EmployeeRepository;
import com.acme.salary.support.DatabaseTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

/**
 * Aggregates run as SQL, so they are tested against MySQL with small, hand-checkable data.
 */
@DatabaseTest
@Import(InsightsRepository.class)
class InsightsRepositoryTest {

    @Autowired
    private InsightsRepository insights;

    @Autowired
    private EmployeeRepository employees;

    private int sequence;

    @BeforeEach
    void setUp() {
        // India: 4 people, so the median is the mean of the middle two: (200 + 300) / 2 = 250.
        hire(Country.INDIA, "Software Engineer", "Engineering", "100");
        hire(Country.INDIA, "Software Engineer", "Engineering", "300");
        hire(Country.INDIA, "Product Manager", "Product", "200");
        hire(Country.INDIA, "Engineering Manager", "Engineering", "1000");
        // United States: 3 people, so the median is the middle value: 70.
        hire(Country.UNITED_STATES, "Software Engineer", "Engineering", "90");
        hire(Country.UNITED_STATES, "Software Engineer", "Engineering", "50");
        hire(Country.UNITED_STATES, "Product Manager", "Product", "70");
        employees.flush();
    }

    @Test
    void summarisesPayPerCountryInLocalCurrency() {
        assertThat(insights.byCountry()).containsExactly(
                stats("IN", "INR", 4, "100.00", "1000.00", "400.00", "250.00", "1600.00"),
                stats("US", "USD", 3, "50.00", "90.00", "70.00", "70.00", "210.00"));
    }

    @Test
    void breaksDownPayByJobTitleWithinACountry() {
        assertThat(insights.byJobTitle(Country.INDIA)).containsExactly(
                stats("Software Engineer", "INR", 2, "100.00", "300.00", "200.00", "200.00", "400.00"),
                stats("Engineering Manager", "INR", 1, "1000.00", "1000.00", "1000.00", "1000.00", "1000.00"),
                stats("Product Manager", "INR", 1, "200.00", "200.00", "200.00", "200.00", "200.00"));
    }

    @Test
    void breaksDownPayByDepartmentWithinACountry() {
        // Engineering in India: 100, 300 and 1000, so the median is the middle value, 300.
        assertThat(insights.byDepartment(Country.INDIA)).containsExactly(
                stats("Engineering", "INR", 3, "100.00", "1000.00", "466.67", "300.00", "1400.00"),
                stats("Product", "INR", 1, "200.00", "200.00", "200.00", "200.00", "200.00"));
    }

    @Test
    void findsNoBreakdownForACountryWithoutEmployees() {
        assertThat(insights.byJobTitle(Country.GERMANY)).isEmpty();
        assertThat(insights.byDepartment(Country.GERMANY)).isEmpty();
    }

    private void hire(Country country, String jobTitle, String department, String salary) {
        sequence++;
        employees.save(new Employee(new EmployeeDetails(
                "Employee " + sequence,
                "employee" + sequence + "@acme.example",
                jobTitle,
                department,
                country,
                new BigDecimal(salary),
                LocalDate.of(2020, 1, 1))));
    }

    private static SalaryStats stats(String group, String currency, long headcount, String minimum,
                                     String maximum, String average, String median, String totalPayroll) {
        return new SalaryStats(group, currency, headcount, new BigDecimal(minimum), new BigDecimal(maximum),
                new BigDecimal(average), new BigDecimal(median), new BigDecimal(totalPayroll));
    }
}
