package com.acme.salary.insights;

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
        return null;
    }
}
