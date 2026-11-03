package com.acme.salary.employee;

import com.acme.salary.common.Country;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeDetails(
        String fullName,
        String email,
        String jobTitle,
        String department,
        Country country,
        BigDecimal annualSalary,
        LocalDate hireDate) {
}
