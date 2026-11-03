package com.acme.salary.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String fullName,
        String email,
        String jobTitle,
        String department,
        String countryCode,
        String countryName,
        String currency,
        BigDecimal annualSalary,
        LocalDate hireDate) {

    static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getJobTitle(),
                employee.getDepartment(),
                employee.getCountry().code(),
                employee.getCountry().displayName(),
                employee.getCurrency().getCurrencyCode(),
                employee.getAnnualSalary(),
                employee.getHireDate());
    }
}
