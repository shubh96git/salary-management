package com.acme.salary.insights;

import com.acme.salary.common.Country;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Salary statistics computed in SQL, so the application never loads every employee to aggregate them.
 */
@Repository
public class InsightsRepository {

    /*
     * One query shape for every breakdown; only the grouping column and the country filter change.
     * Grouping columns come from the constants below, never from user input.
     *
     * MySQL has no MEDIAN function. Each salary is ranked within its group; the median is the middle
     * row for an odd headcount, or the mean of the two middle rows for an even headcount:
     * rows FLOOR((n + 1) / 2) and CEIL((n + 1) / 2), which are the same row when n is odd.
     */
    private static final String STATS_TEMPLATE = """
            WITH ranked AS (
                SELECT %1$s AS group_name, currency, annual_salary,
                       ROW_NUMBER() OVER (PARTITION BY %1$s ORDER BY annual_salary) AS position,
                       COUNT(*) OVER (PARTITION BY %1$s) AS group_size
                FROM employee
                %2$s
            )
            SELECT group_name,
                   currency,
                   COUNT(*) AS headcount,
                   MIN(annual_salary) AS minimum,
                   MAX(annual_salary) AS maximum,
                   ROUND(AVG(annual_salary), 2) AS average,
                   ROUND(AVG(CASE WHEN position IN (FLOOR((group_size + 1) / 2), CEIL((group_size + 1) / 2))
                                  THEN annual_salary END), 2) AS median,
                   SUM(annual_salary) AS total_payroll
            FROM ranked
            GROUP BY group_name, currency
            ORDER BY headcount DESC, group_name
            """;

    private static final String ALL_EMPLOYEES = "";
    private static final String IN_COUNTRY = "WHERE country_code = :country";

    private final JdbcClient jdbc;

    public InsightsRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** One entry per country, in local currency, largest headcount first. */
    public List<SalaryStats> byCountry() {
        return jdbc.sql(STATS_TEMPLATE.formatted("country_code", ALL_EMPLOYEES))
                .query(InsightsRepository::toStats)
                .list();
    }

    /** One entry per job title in the country, largest headcount first. */
    public List<SalaryStats> byJobTitle(Country country) {
        return statsWithin(country, "job_title");
    }

    /** One entry per department in the country, largest headcount first. */
    public List<SalaryStats> byDepartment(Country country) {
        return statsWithin(country, "department");
    }

    private List<SalaryStats> statsWithin(Country country, String groupColumn) {
        return jdbc.sql(STATS_TEMPLATE.formatted(groupColumn, IN_COUNTRY))
                .param("country", country.code())
                .query(InsightsRepository::toStats)
                .list();
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
