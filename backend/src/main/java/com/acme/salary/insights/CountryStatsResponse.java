package com.acme.salary.insights;

import com.acme.salary.common.Country;
import java.math.BigDecimal;

public record CountryStatsResponse(
        String countryCode,
        String countryName,
        String currency,
        long headcount,
        BigDecimal minimum,
        BigDecimal maximum,
        BigDecimal average,
        BigDecimal median,
        BigDecimal totalPayroll) {

    static CountryStatsResponse from(SalaryStats stats) {
        Country country = Country.fromCode(stats.group())
                .orElseThrow(() -> new IllegalStateException("Unsupported country code in database: " + stats.group()));
        return new CountryStatsResponse(
                country.code(),
                country.displayName(),
                stats.currency(),
                stats.headcount(),
                stats.minimum(),
                stats.maximum(),
                stats.average(),
                stats.median(),
                stats.totalPayroll());
    }
}
