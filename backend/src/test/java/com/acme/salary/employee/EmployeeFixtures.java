package com.acme.salary.employee;

import com.acme.salary.common.Country;
import java.math.BigDecimal;
import java.time.LocalDate;

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

        Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        Builder withCountry(Country country) {
            this.country = country;
            return this;
        }

        Builder withHireDate(LocalDate hireDate) {
            this.hireDate = hireDate;
            return this;
        }

        EmployeeDetails details() {
            return new EmployeeDetails(fullName, email, jobTitle, department, country, annualSalary, hireDate);
        }
    }
}
