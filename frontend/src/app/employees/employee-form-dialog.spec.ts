import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectHarness } from '@angular/material/select/testing';
import { Observable, of, throwError } from 'rxjs';
import { EmployeeApi } from './employee-api';
import { EmployeeFormData, EmployeeFormDialog } from './employee-form-dialog';
import { Employee } from './employee.model';
import { FILTER_OPTIONS, anEmployee } from './testing/employee-fixtures';

describe('EmployeeFormDialog', () => {
  let loader: HarnessLoader;
  let api: { create: ReturnType<typeof vi.fn>; update: ReturnType<typeof vi.fn> };
  let dialogRef: { close: ReturnType<typeof vi.fn> };

  async function open(data: EmployeeFormData, response: Observable<Employee> = of(anEmployee())) {
    api = { create: vi.fn(() => response), update: vi.fn(() => response) };
    dialogRef = { close: vi.fn() };
    TestBed.configureTestingModule({
      imports: [EmployeeFormDialog],
      providers: [
        { provide: EmployeeApi, useValue: api },
        { provide: MatDialogRef, useValue: dialogRef },
        { provide: MAT_DIALOG_DATA, useValue: data },
      ],
    });
    const fixture = TestBed.createComponent(EmployeeFormDialog);
    loader = TestbedHarnessEnvironment.loader(fixture);
    await fixture.whenStable();
  }

  async function input(name: string) {
    return loader.getHarness(MatInputHarness.with({ selector: `[formControlName=${name}]` }));
  }

  async function errorsOf(name: string) {
    const field = await loader.getHarness(MatFormFieldHarness.with({ selector: `.field-${name}` }));
    return field.getTextErrors();
  }

  async function fillValidForm() {
    await (await input('fullName')).setValue('Asha Rao');
    await (await input('email')).setValue('asha.rao@acme.example');
    await (await input('jobTitle')).setValue('Software Engineer');
    await (await input('department')).setValue('Engineering');
    await (await loader.getHarness(MatSelectHarness)).clickOptions({ text: 'India' });
    await (await input('annualSalary')).setValue('1500000');
    await (await input('hireDate')).setValue('2021-04-01');
  }

  async function save() {
    await (await loader.getHarness(MatButtonHarness.with({ text: 'Save' }))).click();
  }

  function serverError(status: number, body: object) {
    return throwError(() => new HttpErrorResponse({ status, error: body }));
  }

  it('creates an employee from a valid form and closes with the result', async () => {
    const created = anEmployee({ id: 42 });
    await open({ options: FILTER_OPTIONS }, of(created));

    await fillValidForm();
    await save();

    expect(api.create).toHaveBeenCalledWith({
      fullName: 'Asha Rao',
      email: 'asha.rao@acme.example',
      jobTitle: 'Software Engineer',
      department: 'Engineering',
      countryCode: 'IN',
      annualSalary: 1500000,
      hireDate: '2021-04-01',
    });
    expect(dialogRef.close).toHaveBeenCalledWith(created);
  });

  it('starts from the existing values when editing and saves changes to that employee', async () => {
    await open({ employee: anEmployee({ id: 7 }), options: FILTER_OPTIONS });
    expect(await (await input('fullName')).getValue()).toBe('Asha Rao');

    await (await input('annualSalary')).setValue('1600000');
    await save();

    expect(api.update).toHaveBeenCalledWith(7, expect.objectContaining({ annualSalary: 1600000 }));
    expect(api.create).not.toHaveBeenCalled();
  });

  it('shows what is missing instead of submitting an incomplete form', async () => {
    await open({ options: FILTER_OPTIONS });

    await save();

    expect(api.create).not.toHaveBeenCalled();
    expect(await errorsOf('fullName')).toEqual(['Required']);
    expect(await errorsOf('annualSalary')).toEqual(['Required']);
  });

  it('shows the server’s message next to each invalid field', async () => {
    await open(
      { options: FILTER_OPTIONS },
      serverError(400, { errors: { email: 'must be a well-formed email address' } }),
    );

    await fillValidForm();
    await save();

    expect(await errorsOf('email')).toEqual(['must be a well-formed email address']);
    expect(dialogRef.close).not.toHaveBeenCalled();
  });

  it('explains when the email already belongs to someone else', async () => {
    await open(
      { options: FILTER_OPTIONS },
      serverError(409, { detail: 'An employee with email asha.rao@acme.example already exists' }),
    );

    await fillValidForm();
    await save();

    expect(await errorsOf('email')).toEqual([
      'An employee with email asha.rao@acme.example already exists',
    ]);
  });

  it('shows the salary currency of the chosen country', async () => {
    await open({ options: FILTER_OPTIONS });

    await (await loader.getHarness(MatSelectHarness)).clickOptions({ text: 'United States' });

    const salary = await loader.getHarness(
      MatFormFieldHarness.with({ selector: '.field-annualSalary' }),
    );
    expect(await salary.getTextHints()).toEqual(['Annual gross, in USD']);
  });
});
