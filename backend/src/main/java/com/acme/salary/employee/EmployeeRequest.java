package com.acme.salary.employee;

import com.acme.salary.common.Country;
import com.acme.salary.common.UnknownCountryException;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Body of create and update requests. Limits mirror the database columns.
 */
public record EmployeeRequest(
        @NotBlank @Size(max = 100) String fullName,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(max = 100) String jobTitle,
        @NotBlank @Size(max = 100) String department,
        @NotBlank String countryCode,
        @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal annualSalary,
        @NotNull LocalDate hireDate) {

    EmployeeDetails toDetails() {
        Country country = Country.fromCode(countryCode)
                .orElseThrow(() -> new UnknownCountryException(countryCode));
        return new EmployeeDetails(
                fullName.strip(),
                email.strip(),
                jobTitle.strip(),
                department.strip(),
                country,
                annualSalary,
                hireDate);
    }
}
