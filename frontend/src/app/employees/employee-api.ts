import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';
import { Employee, EmployeeQuery, EmployeeRequest, FilterOptions, Page } from './employee.model';

@Injectable({ providedIn: 'root' })
export class EmployeeApi {
  private readonly http = inject(HttpClient);

  list(query: EmployeeQuery): Observable<Page<Employee>> {
    return EMPTY;
  }

  create(request: EmployeeRequest): Observable<Employee> {
    return EMPTY;
  }

  update(id: number, request: EmployeeRequest): Observable<Employee> {
    return EMPTY;
  }

  delete(id: number): Observable<void> {
    return EMPTY;
  }

  filterOptions(): Observable<FilterOptions> {
    return EMPTY;
  }
}
