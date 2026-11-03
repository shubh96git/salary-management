import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonToggleHarness } from '@angular/material/button-toggle/testing';
import { MatTableHarness } from '@angular/material/table/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { InsightsApi } from './insights-api';
import { InsightsPage } from './insights-page';
import { CountryStats, SalaryStats } from './insights.model';

const INDIA: CountryStats = {
  countryCode: 'IN',
  countryName: 'India',
  currency: 'INR',
  headcount: 6000,
  minimum: 997600,
  maximum: 5016100,
  average: 2458860.59,
  median: 2321100,
  totalPayroll: 14753163540,
};

const UNITED_STATES: CountryStats = {
  countryCode: 'US',
  countryName: 'United States',
  currency: 'USD',
  headcount: 4000,
  minimum: 45200,
  maximum: 229900,
  average: 110429.46,
  median: 105750,
  totalPayroll: 441717840,
};

function statsFor(group: string, median: number): SalaryStats {
  return {
    group,
    currency: 'INR',
    headcount: 10,
    minimum: median - 1000,
    maximum: median + 1000,
    average: median,
    median,
    totalPayroll: median * 10,
  };
}

describe('InsightsPage', () => {
  let fixture: ComponentFixture<InsightsPage>;
  let loader: HarnessLoader;
  let api: { byCountry: ReturnType<typeof vi.fn>; breakdown: ReturnType<typeof vi.fn> };

  async function render(country?: string) {
    api = {
      byCountry: vi.fn(() => of([INDIA, UNITED_STATES])),
      breakdown: vi.fn((_country: string, by: string) =>
        of(
          by === 'job-titles'
            ? [
                statsFor('Senior Software Engineer', 3368600),
                statsFor('Software Engineer', 2393600),
              ]
            : [statsFor('Engineering', 2600000)],
        ),
      ),
    };
    TestBed.configureTestingModule({
      imports: [InsightsPage],
      providers: [provideRouter([]), { provide: InsightsApi, useValue: api }],
    });
    fixture = TestBed.createComponent(InsightsPage);
    if (country) {
      fixture.componentRef.setInput('country', country);
    }
    loader = TestbedHarnessEnvironment.loader(fixture);
    await fixture.whenStable();
  }

  function text(): string {
    return (fixture.nativeElement as HTMLElement).textContent ?? '';
  }

  it('summarises headcount across the organisation', async () => {
    await render();

    expect(text()).toContain('10,000 employees across 2 countries');
  });

  it('shows pay per country in local currency', async () => {
    await render();

    const table = await loader.getHarness(MatTableHarness.with({ selector: '.countries' }));
    const columns = await table.getCellTextByColumnName();
    expect(columns['country'].text).toEqual(['India', 'United States']);
    expect(columns['median'].text).toEqual(['₹2,321,100', '$105,750']);
  });

  it('links each country to its own breakdown', async () => {
    await render();

    const links = (fixture.nativeElement as HTMLElement).querySelectorAll('.countries a');
    expect(Array.from(links).map((link) => link.getAttribute('href'))).toEqual([
      '/insights/IN',
      '/insights/US',
    ]);
  });

  it('asks for a country before showing a breakdown', async () => {
    await render();

    expect(text()).toContain('Select a country');
    expect(api.breakdown).not.toHaveBeenCalled();
  });

  it('shows pay by job title for the selected country', async () => {
    await render('in');

    expect(api.breakdown).toHaveBeenCalledWith('IN', 'job-titles');
    const table = await loader.getHarness(MatTableHarness.with({ selector: '.breakdown' }));
    const columns = await table.getCellTextByColumnName();
    expect(columns['group'].text).toEqual(['Senior Software Engineer', 'Software Engineer']);
    expect(text()).toContain('India');
  });

  it('switches the breakdown to departments', async () => {
    await render('IN');

    await (await loader.getHarness(MatButtonToggleHarness.with({ text: 'Departments' }))).check();

    expect(api.breakdown).toHaveBeenLastCalledWith('IN', 'departments');
    const table = await loader.getHarness(MatTableHarness.with({ selector: '.breakdown' }));
    expect((await table.getCellTextByColumnName())['group'].text).toEqual(['Engineering']);
  });
});
