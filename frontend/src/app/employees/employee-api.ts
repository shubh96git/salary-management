import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Employee, EmployeeQuery, EmployeeRequest, FilterOptions, Page } from './employee.model';

const EMPLOYEES = '/api/employees';

@Injectable({ providedIn: 'root' })
export class EmployeeApi {
  private readonly http = inject(HttpClient);

  list(query: EmployeeQuery): Observable<Page<Employee>> {
    let params = new HttpParams()
      .set('page', query.page)
      .set('size', query.size)
      .set('sort', `${query.sort},${query.direction}`);
    for (const filter of ['search', 'country', 'jobTitle', 'department'] as const) {
      const value = query[filter]?.trim();
      if (value) {
        params = params.set(filter, value);
      }
    }
    return this.http.get<Page<Employee>>(EMPLOYEES, { params });
  }

  create(request: EmployeeRequest): Observable<Employee> {
    return this.http.post<Employee>(EMPLOYEES, request);
  }

  update(id: number, request: EmployeeRequest): Observable<Employee> {
    return this.http.put<Employee>(`${EMPLOYEES}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${EMPLOYEES}/${id}`);
  }

  filterOptions(): Observable<FilterOptions> {
    return this.http.get<FilterOptions>(`${EMPLOYEES}/filter-options`);
  }
}
