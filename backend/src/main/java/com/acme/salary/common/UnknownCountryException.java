package com.acme.salary.common;

public class UnknownCountryException extends InvalidRequestException {

    public UnknownCountryException(String code) {
        super("Unknown country code: " + code);
    }
}
