import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { Component, computed, inject, input, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { InsightsApi } from './insights-api';
import { Breakdown, SalaryStats } from './insights.model';

/** Position of one group's pay range on a shared scale, as CSS percentages. */
interface RangeBar {
  left: number;
  width: number;
  median: number;
}

@Component({
  selector: 'app-insights-page',
  imports: [
    CurrencyPipe,
    DecimalPipe,
    RouterLink,
    MatButtonToggleModule,
    MatProgressBarModule,
    MatTableModule,
  ],
  templateUrl: './insights-page.html',
  styleUrl: './insights-page.scss',
})
export class InsightsPage {
  private readonly api = inject(InsightsApi);

  /** ISO country code from the route (/insights/:country), if one is selected. */
  readonly country = input<string>();

  protected readonly countryColumns = [
    'country',
    'headcount',
    'median',
    'average',
    'range',
    'totalPayroll',
  ];
  protected readonly breakdownColumns = [
    'group',
    'headcount',
    'median',
    'average',
    'range',
    'spread',
  ];

  protected readonly breakdownBy = signal<Breakdown>('job-titles');

  protected readonly countries = rxResource({ stream: () => this.api.byCountry() });

  protected readonly totalHeadcount = computed(() =>
    (this.countries.value() ?? []).reduce((total, country) => total + country.headcount, 0),
  );

  protected readonly selectedCode = computed(() => this.country()?.toUpperCase());

  protected readonly selected = computed(() =>
    this.countries.value()?.find((country) => country.countryCode === this.selectedCode()),
  );

  protected readonly breakdown = rxResource({
    params: () => {
      const country = this.selectedCode();
      return country ? { country, by: this.breakdownBy() } : undefined;
    },
    stream: ({ params }) => this.api.breakdown(params.country, params.by),
  });

  /** Every bar in the breakdown shares one scale, so ranges can be compared across rows. */
  private readonly scale = computed(() => {
    const rows = this.breakdown.value() ?? [];
    return {
      low: Math.min(...rows.map((row) => row.minimum)),
      high: Math.max(...rows.map((row) => row.maximum)),
    };
  });

  protected rangeBar(row: SalaryStats): RangeBar {
    const { low, high } = this.scale();
    const span = high - low || 1;
    const percent = (value: number) => ((value - low) / span) * 100;
    return {
      left: percent(row.minimum),
      width: Math.max(percent(row.maximum) - percent(row.minimum), 0.5),
      median: percent(row.median),
    };
  }
}
