import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { rxResource, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { debounceTime, distinctUntilChanged, filter, switchMap } from 'rxjs';
import { ConfirmDialog, ConfirmDialogData } from '../shared/confirm-dialog';
import { EmployeeApi } from './employee-api';
import { EmployeeFormData, EmployeeFormDialog } from './employee-form-dialog';
import { Employee, EmployeeQuery, FilterOptions } from './employee.model';

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

const NO_OPTIONS: FilterOptions = { countries: [], jobTitles: [], departments: [] };

const SEARCH_DELAY_MS = 300;

type Filter = 'country' | 'jobTitle' | 'department';

@Component({
  selector: 'app-employees-page',
  imports: [
    CurrencyPipe,
    DatePipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatSelectModule,
    MatSortModule,
    MatTableModule,
  ],
  templateUrl: './employees-page.html',
  styleUrl: './employees-page.scss',
})
export class EmployeesPage {
  private readonly api = inject(EmployeeApi);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly columns = [
    'fullName',
    'jobTitle',
    'department',
    'country',
    'annualSalary',
    'hireDate',
    'actions',
  ];
  protected readonly pageSizes = [20, 50, 100];

  protected readonly query = signal<EmployeeQuery>(FIRST_PAGE);
  protected readonly search = new FormControl('', { nonNullable: true });

  private readonly filterOptions = rxResource({ stream: () => this.api.filterOptions() });
  protected readonly options = computed(() => this.filterOptions.value() ?? NO_OPTIONS);

  protected readonly employees = rxResource({
    params: () => this.query(),
    stream: ({ params }) => this.api.list(params),
  });

  protected readonly rows = computed(() => this.employees.value()?.content ?? []);
  protected readonly total = computed(() => this.employees.value()?.totalElements ?? 0);
  protected readonly hasFilters = computed(() => {
    const { search, country, jobTitle, department } = this.query();
    return Boolean(search || country || jobTitle || department);
  });

  constructor() {
    this.search.valueChanges
      .pipe(debounceTime(SEARCH_DELAY_MS), distinctUntilChanged(), takeUntilDestroyed())
      .subscribe((search) => this.narrow({ search }));
  }

  protected filterBy(filter: Filter, value: string): void {
    this.narrow({ [filter]: value });
  }

  protected changePage(event: PageEvent): void {
    this.query.update((query) => ({ ...query, page: event.pageIndex, size: event.pageSize }));
  }

  protected changeSort(sort: Sort): void {
    // Material cycles asc → desc → none; "none" falls back to the default order.
    this.query.update((query) => ({
      ...query,
      sort: sort.direction ? sort.active : FIRST_PAGE.sort,
      direction: sort.direction || FIRST_PAGE.direction,
      page: 0,
    }));
  }

  protected clearFilters(): void {
    this.search.setValue('', { emitEvent: false });
    this.narrow({ search: '', country: '', jobTitle: '', department: '' });
  }

  protected openForm(employee?: Employee): void {
    this.dialog
      .open<EmployeeFormDialog, EmployeeFormData, Employee>(EmployeeFormDialog, {
        data: { employee, options: this.options() },
        width: '640px',
        autoFocus: 'first-tabbable',
      })
      .afterClosed()
      .pipe(filter((saved): saved is Employee => saved !== undefined))
      .subscribe((saved) => {
        this.refresh();
        this.notify(`Saved ${saved.fullName}`);
      });
  }

  protected confirmDelete(employee: Employee): void {
    this.dialog
      .open<ConfirmDialog, ConfirmDialogData, boolean>(ConfirmDialog, {
        data: {
          title: 'Delete employee?',
          message: `${employee.fullName} and their salary record will be permanently removed.`,
          confirmLabel: 'Delete',
        },
      })
      .afterClosed()
      .pipe(
        filter((confirmed) => confirmed === true),
        switchMap(() => this.api.delete(employee.id)),
      )
      .subscribe({
        next: () => {
          this.refresh();
          this.notify(`Deleted ${employee.fullName}`);
        },
        error: () => this.notify(`Could not delete ${employee.fullName}. Please try again.`),
      });
  }

  /** Any change to what is shown starts again from the first page. */
  private narrow(changes: Partial<EmployeeQuery>): void {
    this.query.update((query) => ({ ...query, ...changes, page: 0 }));
  }

  /** Reloads the list, and the filter options in case a new job title or department appeared. */
  private refresh(): void {
    this.employees.reload();
    this.filterOptions.reload();
  }

  private notify(message: string): void {
    this.snackBar.open(message, undefined, { duration: 4000 });
  }
}
