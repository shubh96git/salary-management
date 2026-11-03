package com.acme.salary.employee;

import static com.acme.salary.employee.EmployeeFixtures.anEmployee;
import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salary.common.Country;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void isPaidInTheCurrencyOfItsCountry() {
        Employee employee = new Employee(anEmployee().withCountry(Country.INDIA).details());

        assertThat(employee.getCurrency()).isEqualTo(Currency.getInstance("INR"));
    }

    @Test
    void switchesCurrencyWhenMovedToAnotherCountry() {
        Employee employee = new Employee(anEmployee().withCountry(Country.INDIA).details());

        employee.update(anEmployee().withCountry(Country.GERMANY).details());

        assertThat(employee.getCurrency()).isEqualTo(Currency.getInstance("EUR"));
    }
}
