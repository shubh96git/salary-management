package com.acme.salary.employee;

import com.acme.salary.common.Country;
import com.acme.salary.common.PageResponse;
import com.acme.salary.common.UnsupportedSortException;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Set<String> SORTABLE_FIELDS =
            Set.of("fullName", "email", "jobTitle", "department", "country", "annualSalary", "hireDate");

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<EmployeeResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String department,
            @PageableDefault(size = 20, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        rejectUnsupportedSort(pageable.getSort());
        EmployeeSearchCriteria criteria =
                new EmployeeSearchCriteria(search, toCountry(country), jobTitle, department);
        return PageResponse.from(service.search(criteria, pageable), EmployeeResponse::from);
    }

    @GetMapping("/filter-options")
    public FilterOptions filterOptions() {
        return null;
    }

    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable Long id) {
        return EmployeeResponse.from(service.get(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        Employee created = service.create(request.toDetails());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(EmployeeResponse.from(created));
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return EmployeeResponse.from(service.update(id, request.toDetails()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private static Country toCountry(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return Country.require(code);
    }

    private static void rejectUnsupportedSort(Sort sort) {
        sort.stream()
                .map(Sort.Order::getProperty)
                .filter(property -> !SORTABLE_FIELDS.contains(property))
                .findFirst()
                .ifPresent(property -> {
                    throw new UnsupportedSortException(property);
                });
    }
}
