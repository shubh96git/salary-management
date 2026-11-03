package com.acme.salary.employee;

import static com.acme.salary.employee.EmployeeFixtures.anEmployee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.acme.salary.common.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private EmployeeService service;

    @Test
    void returnsEmployeeById() {
        when(service.get(7L)).thenReturn(anEmployee()
                .withEmail("asha.rao@acme.example")
                .withCountry(Country.INDIA)
                .persistedWithId(7L));

        assertThat(mvc.get().uri("/api/employees/7"))
                .hasStatusOk()
                .bodyJson()
                .isLenientlyEqualTo("""
                        {
                          "id": 7,
                          "email": "asha.rao@acme.example",
                          "countryCode": "IN",
                          "countryName": "India",
                          "currency": "INR",
                          "annualSalary": 1500000.00,
                          "hireDate": "2021-04-01"
                        }
                        """);
    }

    @Test
    void answersNotFoundWithProblemDetailsForUnknownEmployee() {
        when(service.get(99L)).thenThrow(new EmployeeNotFoundException(99L));

        assertThat(mvc.get().uri("/api/employees/99"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("Employee 99 not found");
    }
}
