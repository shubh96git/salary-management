package com.acme.salary.employee;

import com.acme.salary.common.NotFoundException;

public class EmployeeNotFoundException extends NotFoundException {

    public EmployeeNotFoundException(Long id) {
        super("Employee " + id + " not found");
    }
}
