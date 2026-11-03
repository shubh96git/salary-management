import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatTableHarness } from '@angular/material/table/testing';
import { of } from 'rxjs';
import { EmployeeApi } from './employee-api';
import { Employee, Page } from './employee.model';
import { EmployeesPage } from './employees-page';
import { FILTER_OPTIONS, aPageOf, anEmployee } from './testing/employee-fixtures';

describe('EmployeesPage', () => {
  let fixture: ComponentFixture<EmployeesPage>;
  let loader: HarnessLoader;
  let api: { list: ReturnType<typeof vi.fn>; filterOptions: ReturnType<typeof vi.fn> };

  async function render(page: Page<Employee>) {
    api = {
      list: vi.fn(() => of(page)),
      filterOptions: vi.fn(() => of(FILTER_OPTIONS)),
    };
    TestBed.configureTestingModule({
      imports: [EmployeesPage],
      providers: [{ provide: EmployeeApi, useValue: api }],
    });
    fixture = TestBed.createComponent(EmployeesPage);
    loader = TestbedHarnessEnvironment.loader(fixture);
    await fixture.whenStable();
  }

  it('loads the first page of employees sorted by name', async () => {
    await render(aPageOf([anEmployee()]));

    expect(api.list).toHaveBeenCalledWith({
      search: '',
      country: '',
      jobTitle: '',
      department: '',
      page: 0,
      size: 20,
      sort: 'fullName',
      direction: 'asc',
    });
  });

  it('shows each employee with their salary in local currency', async () => {
    await render(
      aPageOf([
        anEmployee(),
        anEmployee({
          id: 2,
          fullName: 'James Smith',
          countryName: 'United States',
          currency: 'USD',
          annualSalary: 110000,
        }),
      ]),
    );

    const table = await loader.getHarness(MatTableHarness);
    const rows = await table.getCellTextByColumnName();
    expect(rows['fullName'].text).toEqual(['Asha Rao', 'James Smith']);
    expect(rows['country'].text).toEqual(['India', 'United States']);
    expect(rows['annualSalary'].text).toEqual(['₹1,500,000', '$110,000']);
  });

  it('explains when no employees match', async () => {
    await render(aPageOf([]));

    expect((fixture.nativeElement as HTMLElement).textContent).toContain('No employees match');
  });
});
