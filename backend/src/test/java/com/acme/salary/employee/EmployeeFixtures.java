package com.acme.salary.employee;

import com.acme.salary.common.Country;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Valid employee data for tests; each test overrides only what it cares about.
 */
final class EmployeeFixtures {

    private EmployeeFixtures() {
    }

    static Builder anEmployee() {
        return new Builder();
    }

    static final class Builder {

        private String fullName = "Asha Rao";
        private String email = "asha.rao@acme.example";
        private String jobTitle = "Software Engineer";
        private String department = "Engineering";
        private Country country = Country.INDIA;
        private BigDecimal annualSalary = new BigDecimal("1500000.00");
        private LocalDate hireDate = LocalDate.of(2021, 4, 1);

        Builder withFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        Builder withJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
            return this;
        }

        Builder withDepartment(String department) {
            this.department = department;
            return this;
        }

        Builder withCountry(Country country) {
            this.country = country;
            return this;
        }

        Builder withAnnualSalary(BigDecimal annualSalary) {
            this.annualSalary = annualSalary;
            return this;
        }

        Builder withHireDate(LocalDate hireDate) {
            this.hireDate = hireDate;
            return this;
        }

        EmployeeDetails details() {
            return new EmployeeDetails(fullName, email, jobTitle, department, country, annualSalary, hireDate);
        }

        /** An employee as if loaded from the database, with its id assigned. */
        Employee persistedWithId(long id) {
            Employee employee = new Employee(details());
            ReflectionTestUtils.setField(employee, "id", id);
            return employee;
        }
    }
}
