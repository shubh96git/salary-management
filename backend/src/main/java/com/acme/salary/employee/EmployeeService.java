package com.acme.salary.employee;

import java.time.Clock;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository repository;
    private final Clock clock;

    public EmployeeService(EmployeeRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public Page<Employee> search(EmployeeSearchCriteria criteria, Pageable pageable) {
        return Page.empty(pageable);
    }

    @Transactional(readOnly = true)
    public Employee get(Long id) {
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public Employee create(EmployeeDetails details) {
        rejectFutureHireDate(details);
        rejectEmailInUse(repository.existsByEmail(details.email()), details.email());
        return repository.save(new Employee(details));
    }

    public Employee update(Long id, EmployeeDetails details) {
        Employee employee = get(id);
        rejectFutureHireDate(details);
        rejectEmailInUse(repository.existsByEmailAndIdNot(details.email(), id), details.email());
        employee.update(details);
        return employee;
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }

    private void rejectFutureHireDate(EmployeeDetails details) {
        if (details.hireDate().isAfter(LocalDate.now(clock))) {
            throw new InvalidEmployeeException("Hire date cannot be in the future");
        }
    }

    private static void rejectEmailInUse(boolean inUse, String email) {
        if (inUse) {
            throw new DuplicateEmailException(email);
        }
    }
}
