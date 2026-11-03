package com.acme.salary.common;

/**
 * The request breaks a business rule; answered with HTTP 400.
 */
public abstract class InvalidRequestException extends RuntimeException {

    protected InvalidRequestException(String message) {
        super(message);
    }
}
