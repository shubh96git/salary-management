package com.acme.salary.employee;

import static com.acme.salary.employee.EmployeeFixtures.anEmployee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.salary.common.Country;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

    @Test
    void createsEmployee() {
        when(service.create(any(EmployeeDetails.class))).thenReturn(anEmployee().persistedWithId(7L));

        assertThat(mvc.post().uri("/api/employees").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
                .hasStatus(HttpStatus.CREATED)
                .hasHeader("Location", "http://localhost/api/employees/7")
                .bodyJson()
                .extractingPath("$.id").isEqualTo(7);

        ArgumentCaptor<EmployeeDetails> details = ArgumentCaptor.forClass(EmployeeDetails.class);
        verify(service).create(details.capture());
        assertThat(details.getValue().country()).isEqualTo(Country.INDIA);
        assertThat(details.getValue().fullName()).isEqualTo("Asha Rao");
    }

    @Test
    void rejectsInvalidFieldsWithOneMessagePerField() {
        String invalid = """
                {
                  "fullName": " ",
                  "email": "not-an-email",
                  "jobTitle": "Software Engineer",
                  "department": "Engineering",
                  "countryCode": "IN",
                  "annualSalary": -10,
                  "hireDate": "2021-04-01"
                }
                """;

        assertThat(mvc.post().uri("/api/employees").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.errors").asMap()
                .containsOnlyKeys("fullName", "email", "annualSalary");
    }

    @Test
    void rejectsUnknownCountry() {
        String unknownCountry = VALID_REQUEST.replace("\"IN\"", "\"XX\"");

        assertThat(mvc.post().uri("/api/employees").contentType(MediaType.APPLICATION_JSON).content(unknownCountry))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("Unknown country code: XX");
    }

    @Test
    void answersConflictForDuplicateEmail() {
        when(service.create(any(EmployeeDetails.class)))
                .thenThrow(new DuplicateEmailException("asha.rao@acme.example"));

        assertThat(mvc.post().uri("/api/employees").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("An employee with email asha.rao@acme.example already exists");
    }

    private static final String VALID_REQUEST = """
            {
              "fullName": "Asha Rao",
              "email": "asha.rao@acme.example",
              "jobTitle": "Software Engineer",
              "department": "Engineering",
              "countryCode": "IN",
              "annualSalary": 1500000.00,
              "hireDate": "2021-04-01"
            }
            """;
}
