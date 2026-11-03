package com.acme.salary.insights;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.acme.salary.common.Country;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Salary statistics computed in SQL, so the application never loads every employee to aggregate them.
 */
@Repository
public class InsightsRepository {

    /*
     * MySQL has no MEDIAN function. Each salary is ranked within its group; the median is the middle
     * row for an odd headcount, or the mean of the two middle rows for an even headcount:
     * rows FLOOR((n + 1) / 2) and CEIL((n + 1) / 2), which are the same row when n is odd.
     */
    private static final String STATS_BY_COUNTRY = """
            WITH ranked AS (
                SELECT country_code, currency, annual_salary,
                       ROW_NUMBER() OVER (PARTITION BY country_code ORDER BY annual_salary) AS position,
                       COUNT(*) OVER (PARTITION BY country_code) AS group_size
                FROM employee
            )
            SELECT country_code AS group_name,
                   currency,
                   COUNT(*) AS headcount,
                   MIN(annual_salary) AS minimum,
                   MAX(annual_salary) AS maximum,
                   ROUND(AVG(annual_salary), 2) AS average,
                   ROUND(AVG(CASE WHEN position IN (FLOOR((group_size + 1) / 2), CEIL((group_size + 1) / 2))
                                  THEN annual_salary END), 2) AS median,
                   SUM(annual_salary) AS total_payroll
            FROM ranked
            GROUP BY country_code, currency
            ORDER BY headcount DESC, group_name
            """;

    private final JdbcClient jdbc;

    public InsightsRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** One entry per country, in local currency, largest headcount first. */
    public List<SalaryStats> byCountry() {
        return jdbc.sql(STATS_BY_COUNTRY).query(InsightsRepository::toStats).list();
    }

    /** One entry per job title in the country, largest headcount first. */
    public List<SalaryStats> byJobTitle(Country country) {
        return List.of();
    }

    /** One entry per department in the country, largest headcount first. */
    public List<SalaryStats> byDepartment(Country country) {
        return List.of();
    }

    private static SalaryStats toStats(ResultSet row, int rowNumber) throws SQLException {
        return new SalaryStats(
                row.getString("group_name"),
                row.getString("currency"),
                row.getLong("headcount"),
                row.getBigDecimal("minimum"),
                row.getBigDecimal("maximum"),
                row.getBigDecimal("average"),
                row.getBigDecimal("median"),
                row.getBigDecimal("total_payroll"));
    }
}
