package com.acme.salary.common;

/**
 * A requested resource does not exist; answered with HTTP 404.
 */
public abstract class NotFoundException extends RuntimeException {

    protected NotFoundException(String message) {
        super(message);
    }
}
