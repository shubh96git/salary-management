package com.acme.salary.employee;

import com.acme.salary.common.Country;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Entity
@Table(name = "employee")
public class Employee {

    private static final int MONEY_SCALE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String jobTitle;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "country_code", nullable = false, columnDefinition = "char(2)")
    private Country country;

    @Column(nullable = false, columnDefinition = "char(3)")
    private Currency currency;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal annualSalary;

    @Column(nullable = false)
    private LocalDate hireDate;

    protected Employee() {
        // required by JPA
    }

    public Employee(EmployeeDetails details) {
        update(details);
    }

    public void update(EmployeeDetails details) {
        this.fullName = details.fullName();
        this.email = details.email();
        this.jobTitle = details.jobTitle();
        this.department = details.department();
        this.country = details.country();
        this.currency = details.country().currency();
        // Without a rounding mode, a value with more than two decimals fails loudly instead of being rounded.
        this.annualSalary = details.annualSalary().setScale(MONEY_SCALE);
        this.hireDate = details.hireDate();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public Country getCountry() {
        return country;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAnnualSalary() {
        return annualSalary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }
}
