package com.acme.salary.seed;

import com.acme.salary.employee.Employee;
import com.acme.salary.employee.EmployeeDetails;
import java.sql.Date;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads employees into an empty database. Running it again never duplicates data.
 *
 * <p>Uses JDBC batch inserts rather than JPA: 10,000 rows load in seconds without filling the
 * persistence context. Each row still passes through {@link Employee}, so seeded data follows
 * the same rules (currency from country, money scale) as data entered through the API.
 */
@Component
public class EmployeeSeeder {

    private static final int BATCH_SIZE = 1_000;

    private static final String INSERT = """
            INSERT INTO employee
                (full_name, email, job_title, department, country_code, currency, annual_salary, hire_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private final JdbcTemplate jdbc;

    public EmployeeSeeder(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * @param employees generated only if the table is empty
     * @return the number of employees inserted
     */
    @Transactional
    public int seedIfEmpty(Supplier<List<EmployeeDetails>> employees) {
        if (hasEmployees()) {
            return 0;
        }
        List<Employee> toInsert = employees.get().stream().map(Employee::new).toList();
        jdbc.batchUpdate(INSERT, toInsert, BATCH_SIZE, (statement, employee) -> {
            statement.setString(1, employee.getFullName());
            statement.setString(2, employee.getEmail());
            statement.setString(3, employee.getJobTitle());
            statement.setString(4, employee.getDepartment());
            statement.setString(5, employee.getCountry().code());
            statement.setString(6, employee.getCurrency().getCurrencyCode());
            statement.setBigDecimal(7, employee.getAnnualSalary());
            statement.setDate(8, Date.valueOf(employee.getHireDate()));
        });
        return toInsert.size();
    }

    private boolean hasEmployees() {
        return Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS (SELECT 1 FROM employee)", Boolean.class));
    }
}
