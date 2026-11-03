package com.acme.salary.seed;

import com.acme.salary.employee.EmployeeDetails;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Loads employees into an empty database. Running it again never duplicates data.
 */
@Component
public class EmployeeSeeder {

    private final JdbcTemplate jdbc;

    public EmployeeSeeder(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * @param employees generated only if the table is empty
     * @return the number of employees inserted
     */
    public int seedIfEmpty(Supplier<List<EmployeeDetails>> employees) {
        return 0;
    }
}
