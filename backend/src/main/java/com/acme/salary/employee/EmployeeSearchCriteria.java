package com.acme.salary.employee;

import com.acme.salary.common.Country;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * Optional filters for the employee list; a null or blank value means "do not filter on this".
 *
 * @param text       matches anywhere in the employee's name or email
 * @param country    exact country
 * @param jobTitle   exact job title
 * @param department exact department
 */
public record EmployeeSearchCriteria(String text, Country country, String jobTitle, String department) {

    // Not a backslash: MySQL also treats backslashes as escapes inside SQL string literals.
    private static final char LIKE_ESCAPE = '!';

    Specification<Employee> toSpecification() {
        List<Specification<Employee>> filters = new ArrayList<>();
        if (isPresent(text)) {
            filters.add(nameOrEmailContains(text.strip()));
        }
        if (country != null) {
            filters.add((root, query, cb) -> cb.equal(root.get("country"), country));
        }
        if (isPresent(jobTitle)) {
            filters.add(attributeEquals("jobTitle", jobTitle.strip()));
        }
        if (isPresent(department)) {
            filters.add(attributeEquals("department", department.strip()));
        }
        return Specification.allOf(filters);
    }

    // Case-insensitivity comes from the column collation (utf8mb4_unicode_ci).
    private static Specification<Employee> nameOrEmailContains(String text) {
        String pattern = "%" + escapeLikeWildcards(text) + "%";
        return (root, query, cb) -> cb.or(
                cb.like(root.get("fullName"), pattern, LIKE_ESCAPE),
                cb.like(root.get("email"), pattern, LIKE_ESCAPE));
    }

    private static Specification<Employee> attributeEquals(String attribute, String value) {
        return (root, query, cb) -> cb.equal(root.get(attribute), value);
    }

    private static String escapeLikeWildcards(String text) {
        return text.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }

    private static boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }
}
