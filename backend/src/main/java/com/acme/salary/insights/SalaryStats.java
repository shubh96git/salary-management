package com.acme.salary.insights;

import java.math.BigDecimal;

/**
 * Pay statistics for one group of employees (a country, or a job title or department within a country).
 * All amounts are in {@code currency}.
 */
public record SalaryStats(
        String group,
        String currency,
        long headcount,
        BigDecimal minimum,
        BigDecimal maximum,
        BigDecimal average,
        BigDecimal median,
        BigDecimal totalPayroll) {
}
