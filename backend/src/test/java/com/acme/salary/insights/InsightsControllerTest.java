package com.acme.salary.insights;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.acme.salary.common.Country;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(InsightsController.class)
class InsightsControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private InsightsRepository insights;

    @Test
    void summarisesPayPerCountryWithCountryNames() {
        when(insights.byCountry()).thenReturn(List.of(stats("IN", "INR")));

        assertThat(mvc.get().uri("/api/insights/countries"))
                .hasStatusOk()
                .bodyJson()
                .isLenientlyEqualTo("""
                        [
                          {
                            "countryCode": "IN",
                            "countryName": "India",
                            "currency": "INR",
                            "headcount": 4,
                            "minimum": 100.00,
                            "maximum": 1000.00,
                            "average": 400.00,
                            "median": 250.00,
                            "totalPayroll": 1600.00
                          }
                        ]
                        """);
    }

    @Test
    void breaksDownPayByJobTitleForTheRequestedCountry() {
        when(insights.byJobTitle(Country.INDIA)).thenReturn(List.of(stats("Software Engineer", "INR")));

        assertThat(mvc.get().uri("/api/insights/countries/in/job-titles"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].group").isEqualTo("Software Engineer");
    }

    @Test
    void breaksDownPayByDepartmentForTheRequestedCountry() {
        when(insights.byDepartment(Country.INDIA)).thenReturn(List.of(stats("Engineering", "INR")));

        assertThat(mvc.get().uri("/api/insights/countries/IN/departments"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].group").isEqualTo("Engineering");
    }

    @Test
    void rejectsUnknownCountry() {
        assertThat(mvc.get().uri("/api/insights/countries/XX/job-titles"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("Unknown country code: XX");
    }

    private static SalaryStats stats(String group, String currency) {
        return new SalaryStats(group, currency, 4, new BigDecimal("100.00"), new BigDecimal("1000.00"),
                new BigDecimal("400.00"), new BigDecimal("250.00"), new BigDecimal("1600.00"));
    }
}
