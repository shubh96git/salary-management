import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { EmployeeApi } from './employee-api';
import { EmployeeRequest } from './employee.model';

describe('EmployeeApi', () => {
  let api: EmployeeApi;
  let http: HttpTestingController;

  const request: EmployeeRequest = {
    fullName: 'Asha Rao',
    email: 'asha.rao@acme.example',
    jobTitle: 'Software Engineer',
    department: 'Engineering',
    countryCode: 'IN',
    annualSalary: 1500000,
    hireDate: '2021-04-01',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    api = TestBed.inject(EmployeeApi);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lists employees sending only the filters that are set', () => {
    api
      .list({
        search: ' rao ',
        country: '',
        jobTitle: undefined,
        department: 'Engineering',
        page: 2,
        size: 20,
        sort: 'annualSalary',
        direction: 'desc',
      })
      .subscribe();

    const call = http.expectOne((req) => req.url === '/api/employees');
    expect(call.request.method).toBe('GET');
    expect(call.request.params.keys().sort()).toEqual([
      'department',
      'page',
      'search',
      'size',
      'sort',
    ]);
    expect(call.request.params.get('search')).toBe('rao');
    expect(call.request.params.get('department')).toBe('Engineering');
    expect(call.request.params.get('page')).toBe('2');
    expect(call.request.params.get('sort')).toBe('annualSalary,desc');
  });

  it('creates an employee', () => {
    api.create(request).subscribe();

    const call = http.expectOne('/api/employees');
    expect(call.request.method).toBe('POST');
    expect(call.request.body).toEqual(request);
  });

  it('updates an employee', () => {
    api.update(7, request).subscribe();

    const call = http.expectOne('/api/employees/7');
    expect(call.request.method).toBe('PUT');
    expect(call.request.body).toEqual(request);
  });

  it('deletes an employee', () => {
    api.delete(7).subscribe();

    expect(http.expectOne('/api/employees/7').request.method).toBe('DELETE');
  });

  it('loads filter options', () => {
    api.filterOptions().subscribe();

    expect(http.expectOne('/api/employees/filter-options').request.method).toBe('GET');
  });
});
