package com.acme.salary.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Currency;
import org.junit.jupiter.api.Test;

class CountryTest {

    @Test
    void findsCountryByIsoCode() {
        assertThat(Country.fromCode("IN")).contains(Country.INDIA);
    }

    @Test
    void findsCountryRegardlessOfCase() {
        assertThat(Country.fromCode("us")).contains(Country.UNITED_STATES);
    }

    @Test
    void findsNothingForUnknownCode() {
        assertThat(Country.fromCode("XX")).isEmpty();
    }

    @Test
    void findsNothingForMissingCode() {
        assertThat(Country.fromCode(null)).isEmpty();
    }

    @Test
    void paysInLocalCurrency() {
        assertThat(Country.INDIA.currency()).isEqualTo(Currency.getInstance("INR"));
        assertThat(Country.GERMANY.currency()).isEqualTo(Currency.getInstance("EUR"));
    }
}
