package com.acme.salary.common;

import java.util.Arrays;
import java.util.Currency;
import java.util.Optional;

public enum Country {
    INDIA("IN", "India", "INR"),
    UNITED_STATES("US", "United States", "USD"),
    UNITED_KINGDOM("GB", "United Kingdom", "GBP"),
    GERMANY("DE", "Germany", "EUR"),
    FRANCE("FR", "France", "EUR"),
    CANADA("CA", "Canada", "CAD"),
    AUSTRALIA("AU", "Australia", "AUD"),
    SINGAPORE("SG", "Singapore", "SGD");

    private final String code;
    private final String displayName;
    private final Currency currency;

    Country(String code, String displayName, String currencyCode) {
        this.code = code;
        this.displayName = displayName;
        this.currency = Currency.getInstance(currencyCode);
    }

    public static Optional<Country> fromCode(String code) {
        return Arrays.stream(values())
                .filter(country -> country.code.equalsIgnoreCase(code))
                .findFirst();
    }

    public String code() {
        return code;
    }

    public String displayName() {
        return displayName;
    }

    public Currency currency() {
        return currency;
    }
}
