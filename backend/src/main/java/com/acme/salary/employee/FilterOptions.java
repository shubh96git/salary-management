package com.acme.salary.employee;

import java.util.List;

/**
 * Values the UI offers in its filters and forms.
 *
 * @param countries   every supported country, whether or not anyone works there yet
 * @param jobTitles   job titles in use, alphabetically
 * @param departments departments in use, alphabetically
 */
public record FilterOptions(List<CountryOption> countries, List<String> jobTitles, List<String> departments) {

    public record CountryOption(String code, String name, String currency) {
    }
}
