package com.acme.salary.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee create(EmployeeDetails details) {
        if (repository.existsByEmail(details.email())) {
            throw new DuplicateEmailException(details.email());
        }
        return repository.save(new Employee(details));
    }
}
