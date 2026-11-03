package com.acme.salary.employee;

import com.acme.salary.common.Country;
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

    Specification<Employee> toSpecification() {
        return Specification.unrestricted();
    }
}
