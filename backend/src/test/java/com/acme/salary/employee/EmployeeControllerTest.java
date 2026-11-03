package com.acme.salary.employee;

import static com.acme.salary.employee.EmployeeFixtures.anEmployee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.salary.common.Country;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    void listsOnePageOfEmployees() {
        when(service.search(any(), any())).thenReturn(
                new PageImpl<>(List.of(anEmployee().persistedWithId(7L)), PageRequest.of(0, 20), 41));

        assertThat(mvc.get().uri("/api/employees"))
                .hasStatusOk()
                .bodyJson()
                .isLenientlyEqualTo("""
                        {
                          "content": [ { "id": 7, "fullName": "Asha Rao" } ],
                          "page": 0,
                          "size": 20,
                          "totalElements": 41,
                          "totalPages": 3
                        }
                        """);
    }

    @Test
    void passesFiltersPagingAndSortingToTheService() {
        when(service.search(any(), any())).thenReturn(Page.empty());

        assertThat(mvc.get().uri("/api/employees?search=rao&country=in&jobTitle=Engineer&department=Product"
                + "&page=2&size=50&sort=annualSalary,desc"))
                .hasStatusOk();

        ArgumentCaptor<EmployeeSearchCriteria> criteria = ArgumentCaptor.forClass(EmployeeSearchCriteria.class);
        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(service).search(criteria.capture(), pageable.capture());
        assertThat(criteria.getValue())
                .isEqualTo(new EmployeeSearchCriteria("rao", Country.INDIA, "Engineer", "Product"));
        assertThat(pageable.getValue())
                .isEqualTo(PageRequest.of(2, 50, Sort.by(Sort.Direction.DESC, "annualSalary")));
    }

    @Test
    void defaultsToFirstTwentyEmployeesSortedByName() {
        when(service.search(any(), any())).thenReturn(Page.empty());

        assertThat(mvc.get().uri("/api/employees")).hasStatusOk();

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(service).search(any(), pageable.capture());
        assertThat(pageable.getValue()).isEqualTo(PageRequest.of(0, 20, Sort.by("fullName")));
    }

    @Test
    void capsPageSizeAtOneHundred() {
        when(service.search(any(), any())).thenReturn(Page.empty());

        assertThat(mvc.get().uri("/api/employees?size=5000")).hasStatusOk();

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(service).search(any(), pageable.capture());
        assertThat(pageable.getValue().getPageSize()).isEqualTo(100);
    }

    @Test
    void rejectsUnknownCountryFilter() {
        assertThat(mvc.get().uri("/api/employees?country=XX"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("Unknown country code: XX");
    }

    @Test
    void rejectsSortingByAnUnsupportedField() {
        assertThat(mvc.get().uri("/api/employees?sort=createdAt"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.detail").asString().contains("createdAt");
    }

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

    @Test
    void answersConflictWhenTheDatabaseRejectsAConcurrentDuplicate() {
        when(service.create(any(EmployeeDetails.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry for key 'uk_employee_email'"));

        assertThat(mvc.post().uri("/api/employees").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("The change conflicts with existing data");
    }

    @Test
    void updatesEmployee() {
        when(service.update(eq(7L), any(EmployeeDetails.class)))
                .thenReturn(anEmployee().withCountry(Country.GERMANY).persistedWithId(7L));

        assertThat(mvc.put().uri("/api/employees/7").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.currency").isEqualTo("EUR");
    }

    @Test
    void answersNotFoundWhenUpdatingUnknownEmployee() {
        when(service.update(eq(99L), any(EmployeeDetails.class))).thenThrow(new EmployeeNotFoundException(99L));

        assertThat(mvc.put().uri("/api/employees/99").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void deletesEmployee() {
        assertThat(mvc.delete().uri("/api/employees/7")).hasStatus(HttpStatus.NO_CONTENT);

        verify(service).delete(7L);
    }

    @Test
    void answersNotFoundWhenDeletingUnknownEmployee() {
        doThrow(new EmployeeNotFoundException(99L)).when(service).delete(99L);

        assertThat(mvc.delete().uri("/api/employees/99")).hasStatus(HttpStatus.NOT_FOUND);
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
