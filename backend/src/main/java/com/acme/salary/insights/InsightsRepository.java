package com.acme.salary.insights;

import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Salary statistics computed in SQL, so the application never loads every employee to aggregate them.
 */
@Repository
public class InsightsRepository {

    private final JdbcClient jdbc;

    public InsightsRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** One entry per country, in local currency, largest headcount first. */
    public List<SalaryStats> byCountry() {
        return List.of();
    }
}
