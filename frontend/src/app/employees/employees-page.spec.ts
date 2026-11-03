import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatPaginatorHarness } from '@angular/material/paginator/testing';
import { MatSelectHarness } from '@angular/material/select/testing';
import { MatSortHarness } from '@angular/material/sort/testing';
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

  describe('narrowing the list', () => {
    const MANY_EMPLOYEES = aPageOf([anEmployee()], 41);

    function lastQuery() {
      return api.list.mock.lastCall?.[0];
    }

    it('searches by name or email once typing pauses', async () => {
      await render(MANY_EMPLOYEES);
      const search = await loader.getHarness(MatInputHarness.with({ selector: '.search' }));

      await search.setValue('rao');
      await new Promise((resolve) => setTimeout(resolve, 350));
      await fixture.whenStable();

      expect(lastQuery()).toMatchObject({ search: 'rao', page: 0 });
    });

    it('filters by country, job title and department from the available options', async () => {
      await render(MANY_EMPLOYEES);

      const country = await loader.getHarness(
        MatSelectHarness.with({ selector: '.country-filter' }),
      );
      await country.open();
      expect((await country.getOptions()).length).toBe(3); // "All countries" + 2 countries
      await country.clickOptions({ text: 'India' });
      const jobTitle = await loader.getHarness(
        MatSelectHarness.with({ selector: '.job-title-filter' }),
      );
      await jobTitle.clickOptions({ text: 'Software Engineer' });
      const department = await loader.getHarness(
        MatSelectHarness.with({ selector: '.department-filter' }),
      );
      await department.clickOptions({ text: 'Engineering' });

      expect(lastQuery()).toMatchObject({
        country: 'IN',
        jobTitle: 'Software Engineer',
        department: 'Engineering',
      });
    });

    it('moves between pages', async () => {
      await render(MANY_EMPLOYEES);
      const paginator = await loader.getHarness(MatPaginatorHarness);

      await paginator.goToNextPage();

      expect(lastQuery()).toMatchObject({ page: 1, size: 20 });
    });

    it('returns to the first page when a filter changes', async () => {
      await render(MANY_EMPLOYEES);
      await (await loader.getHarness(MatPaginatorHarness)).goToNextPage();

      const country = await loader.getHarness(
        MatSelectHarness.with({ selector: '.country-filter' }),
      );
      await country.clickOptions({ text: 'United States' });

      expect(lastQuery()).toMatchObject({ country: 'US', page: 0 });
    });

    it('sorts by a column, then reverses the order on a second click', async () => {
      await render(MANY_EMPLOYEES);
      const salaryHeader = (
        await (
          await loader.getHarness(MatSortHarness)
        ).getSortHeaders({
          label: 'Annual salary',
        })
      )[0];

      await salaryHeader.click();
      expect(lastQuery()).toMatchObject({ sort: 'annualSalary', direction: 'asc' });

      await salaryHeader.click();
      expect(lastQuery()).toMatchObject({ sort: 'annualSalary', direction: 'desc' });
    });

    it('clears every filter at once', async () => {
      await render(MANY_EMPLOYEES);
      const country = await loader.getHarness(
        MatSelectHarness.with({ selector: '.country-filter' }),
      );
      await country.clickOptions({ text: 'India' });

      await (await loader.getHarness(MatButtonHarness.with({ text: 'Clear filters' }))).click();

      expect(lastQuery()).toMatchObject({ search: '', country: '', jobTitle: '', department: '' });
      expect(await country.getValueText()).toBe('All countries');
    });
  });
});
