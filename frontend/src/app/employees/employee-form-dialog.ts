import { formatDate } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { EmployeeApi } from './employee-api';
import { Employee, EmployeeRequest, FilterOptions } from './employee.model';

export interface EmployeeFormData {
  /** Present when editing; absent when adding a new employee. */
  employee?: Employee;
  options: FilterOptions;
}

/** Field limits mirror the API's validation and the database columns. */
const MAX_TEXT = 100;
const MAX_EMAIL = 255;

const MESSAGES: Record<string, string> = {
  required: 'Required',
  email: 'Enter a valid email address',
  maxlength: 'Too long',
  min: 'Must be greater than zero',
};

@Component({
  selector: 'app-employee-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './employee-form-dialog.html',
  styleUrl: './employee-form-dialog.scss',
})
export class EmployeeFormDialog {
  private readonly api = inject(EmployeeApi);
  private readonly dialogRef = inject<MatDialogRef<EmployeeFormDialog, Employee>>(MatDialogRef);
  protected readonly data = inject<EmployeeFormData>(MAT_DIALOG_DATA);

  protected readonly editing = this.data.employee !== undefined;
  // Local date, not toISOString(): that is UTC, which is still "yesterday" for part of the day east of UTC.
  protected readonly today = formatDate(new Date(), 'yyyy-MM-dd', 'en-US');

  protected readonly form = new FormGroup({
    fullName: new FormControl(this.data.employee?.fullName ?? '', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(MAX_TEXT)],
    }),
    email: new FormControl(this.data.employee?.email ?? '', {
      nonNullable: true,
      validators: [Validators.required, Validators.email, Validators.maxLength(MAX_EMAIL)],
    }),
    jobTitle: new FormControl(this.data.employee?.jobTitle ?? '', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(MAX_TEXT)],
    }),
    department: new FormControl(this.data.employee?.department ?? '', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(MAX_TEXT)],
    }),
    countryCode: new FormControl(this.data.employee?.countryCode ?? '', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    annualSalary: new FormControl<number | null>(this.data.employee?.annualSalary ?? null, [
      Validators.required,
      Validators.min(0.01),
    ]),
    hireDate: new FormControl(this.data.employee?.hireDate ?? '', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  private readonly countryCode = toSignal(this.form.controls.countryCode.valueChanges, {
    initialValue: this.form.controls.countryCode.value,
  });
  protected readonly currency = computed(
    () => this.data.options.countries.find((c) => c.code === this.countryCode())?.currency,
  );

  protected readonly saving = signal(false);
  protected readonly failure = signal<string | null>(null);

  protected errorFor(field: keyof typeof this.form.controls): string {
    const errors = this.form.controls[field].errors ?? {};
    const [key] = Object.keys(errors);
    return key === 'server' ? errors['server'] : (MESSAGES[key] ?? 'Invalid value');
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.failure.set(null);
    const request = this.form.getRawValue() as EmployeeRequest;
    const saved = this.data.employee
      ? this.api.update(this.data.employee.id, request)
      : this.api.create(request);
    saved.subscribe({
      next: (employee) => this.dialogRef.close(employee),
      error: (error: HttpErrorResponse) => {
        this.saving.set(false);
        this.showServerError(error);
      },
    });
  }

  /** Puts the server's validation messages on the fields they belong to. */
  private showServerError(error: HttpErrorResponse): void {
    const problem = error.error ?? {};
    if (error.status === 400 && problem.errors) {
      for (const [field, message] of Object.entries<string>(problem.errors)) {
        this.setServerError(field, message);
      }
    } else if (error.status === 409) {
      this.setServerError('email', problem.detail);
    } else {
      this.failure.set(problem.detail ?? 'Something went wrong. Please try again.');
    }
  }

  private setServerError(field: string, message: string): void {
    const control = this.form.get(field);
    if (control) {
      control.setErrors({ server: message });
      control.markAsTouched();
    } else {
      this.failure.set(message);
    }
  }
}
