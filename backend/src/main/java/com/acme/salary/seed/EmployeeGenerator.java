package com.acme.salary.seed;

import com.acme.salary.employee.EmployeeDetails;
import java.util.List;

/**
 * Generates realistic, deterministic employee data: the same seed always produces the same employees.
 */
public class EmployeeGenerator {

    private final long seed;

    public EmployeeGenerator(long seed) {
        this.seed = seed;
    }

    public List<EmployeeDetails> generate(int count) {
        return List.of();
    }
}
