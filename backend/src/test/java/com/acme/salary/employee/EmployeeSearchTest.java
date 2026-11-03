package com.acme.salary.employee;

import static com.acme.salary.employee.EmployeeFixtures.anEmployee;
import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salary.common.Country;
import com.acme.salary.support.DatabaseTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Search filters run as SQL, so they are tested against MySQL rather than mocks.
 */
@DatabaseTest
class EmployeeSearchTest {

    @Autowired
    private EmployeeRepository repository;

    @BeforeEach
    void setUp() {
        save("Asha Rao", "asha.rao@acme.example", "Software Engineer", "Engineering", Country.INDIA);
        save("Meera Rao", "meera.rao@acme.example", "Product Manager", "Product", Country.INDIA);
        save("Vikram Shah", "vikram.shah@acme.example", "Software Engineer", "Engineering", Country.UNITED_STATES);
        save("Liam Walsh", "liam.walsh@acme.example", "Software Engineer", "Platform", Country.UNITED_KINGDOM);
    }

    @Test
    void returnsEveryoneWithoutFilters() {
        assertThat(namesMatching(new EmployeeSearchCriteria(null, null, null, null)))
                .containsExactlyInAnyOrder("Asha Rao", "Meera Rao", "Vikram Shah", "Liam Walsh");
    }

    @Test
    void findsByPartOfNameIgnoringCase() {
        assertThat(namesMatching(new EmployeeSearchCriteria("rAO", null, null, null)))
                .containsExactlyInAnyOrder("Asha Rao", "Meera Rao");
    }

    @Test
    void findsByPartOfEmail() {
        assertThat(namesMatching(new EmployeeSearchCriteria("vikram.shah@", null, null, null)))
                .containsExactly("Vikram Shah");
    }

    @Test
    void filtersByCountry() {
        assertThat(namesMatching(new EmployeeSearchCriteria(null, Country.INDIA, null, null)))
                .containsExactlyInAnyOrder("Asha Rao", "Meera Rao");
    }

    @Test
    void combinesAllFilters() {
        EmployeeSearchCriteria criteria =
                new EmployeeSearchCriteria("rao", Country.INDIA, "Software Engineer", "Engineering");

        assertThat(namesMatching(criteria)).containsExactly("Asha Rao");
    }

    @Test
    void treatsWildcardCharactersInSearchTextLiterally() {
        assertThat(namesMatching(new EmployeeSearchCriteria("%", null, null, null))).isEmpty();
    }

    private List<String> namesMatching(EmployeeSearchCriteria criteria) {
        return repository.findAll(criteria.toSpecification()).stream()
                .map(Employee::getFullName)
                .toList();
    }

    private void save(String fullName, String email, String jobTitle, String department, Country country) {
        repository.save(new Employee(anEmployee()
                .withFullName(fullName)
                .withEmail(email)
                .withJobTitle(jobTitle)
                .withDepartment(department)
                .withCountry(country)
                .details()));
    }
}
