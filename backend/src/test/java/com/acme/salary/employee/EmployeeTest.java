package com.acme.salary.employee;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salary.common.Country;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void isPaidInTheCurrencyOfItsCountry() {
        Employee employee = new Employee(detailsIn(Country.INDIA));

        assertThat(employee.getCurrency()).isEqualTo(Currency.getInstance("INR"));
    }

    @Test
    void switchesCurrencyWhenMovedToAnotherCountry() {
        Employee employee = new Employee(detailsIn(Country.INDIA));

        employee.update(detailsIn(Country.GERMANY));

        assertThat(employee.getCurrency()).isEqualTo(Currency.getInstance("EUR"));
    }

    private static EmployeeDetails detailsIn(Country country) {
        return new EmployeeDetails(
                "Asha Rao",
                "asha.rao@acme.example",
                "Software Engineer",
                "Engineering",
                country,
                new BigDecimal("1500000.00"),
                LocalDate.of(2021, 4, 1));
    }
}
