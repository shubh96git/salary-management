package com.acme.salary.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CountryConverter implements AttributeConverter<Country, String> {

    @Override
    public String convertToDatabaseColumn(Country country) {
        return country == null ? null : country.code();
    }

    @Override
    public Country convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Country.fromCode(code)
                .orElseThrow(() -> new IllegalStateException("Unsupported country code in database: " + code));
    }
}
