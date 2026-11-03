package com.acme.salary.common;

public class UnsupportedSortException extends InvalidRequestException {

    public UnsupportedSortException(String property) {
        super("Cannot sort by '" + property + "'");
    }
}
