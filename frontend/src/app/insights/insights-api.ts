import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';
import { Breakdown, CountryStats, SalaryStats } from './insights.model';

@Injectable({ providedIn: 'root' })
export class InsightsApi {
  private readonly http = inject(HttpClient);

  byCountry(): Observable<CountryStats[]> {
    return EMPTY;
  }

  breakdown(countryCode: string, by: Breakdown): Observable<SalaryStats[]> {
    return EMPTY;
  }
}
