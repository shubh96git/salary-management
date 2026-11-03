package com.acme.salary.insights;

import com.acme.salary.common.Country;
import com.acme.salary.common.UnknownCountryException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Answers "how does the org pay people?". Insights are read-only queries with no business rules,
 * so the controller uses the repository directly rather than a pass-through service.
 */
@RestController
@RequestMapping("/api/insights")
public class InsightsController {

    private final InsightsRepository insights;

    public InsightsController(InsightsRepository insights) {
        this.insights = insights;
    }

    @GetMapping("/countries")
    public List<CountryStatsResponse> byCountry() {
        return insights.byCountry().stream().map(CountryStatsResponse::from).toList();
    }

    @GetMapping("/countries/{countryCode}/job-titles")
    public List<SalaryStats> byJobTitle(@PathVariable String countryCode) {
        return insights.byJobTitle(toCountry(countryCode));
    }

    @GetMapping("/countries/{countryCode}/departments")
    public List<SalaryStats> byDepartment(@PathVariable String countryCode) {
        return insights.byDepartment(toCountry(countryCode));
    }

    private static Country toCountry(String code) {
        return Country.fromCode(code).orElseThrow(() -> new UnknownCountryException(code));
    }
}
