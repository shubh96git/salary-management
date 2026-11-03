package com.acme.salary.employee;

import com.acme.salary.common.InvalidRequestException;

public class InvalidEmployeeException extends InvalidRequestException {

    public InvalidEmployeeException(String message) {
        super(message);
    }
}
