package com.acme.salary.common;

/**
 * The request conflicts with existing data; answered with HTTP 409.
 */
public abstract class ConflictException extends RuntimeException {

    protected ConflictException(String message) {
        super(message);
    }
}
