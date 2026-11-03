package com.acme.salary.employee;

import com.acme.salary.common.ConflictException;

public class DuplicateEmailException extends ConflictException {

    public DuplicateEmailException(String email) {
        super("An employee with email " + email + " already exists");
    }
}
