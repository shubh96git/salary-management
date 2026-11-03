import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { EmployeeApi } from './employee-api';
import { EmployeeQuery } from './employee.model';

const FIRST_PAGE: EmployeeQuery = {
  search: '',
  country: '',
  jobTitle: '',
  department: '',
  page: 0,
  size: 20,
  sort: 'fullName',
  direction: 'asc',
};

@Component({
  selector: 'app-employees-page',
  imports: [CurrencyPipe, DatePipe, MatTableModule, MatProgressBarModule],
  templateUrl: './employees-page.html',
  styleUrl: './employees-page.scss',
})
export class EmployeesPage {
  private readonly api = inject(EmployeeApi);

  protected readonly columns = [
    'fullName',
    'jobTitle',
    'department',
    'country',
    'annualSalary',
    'hireDate',
  ];

  protected readonly query = signal<EmployeeQuery>(FIRST_PAGE);

  protected readonly employees = rxResource({
    params: () => this.query(),
    stream: ({ params }) => this.api.list(params),
  });

  protected readonly rows = computed(() => this.employees.value()?.content ?? []);
}
