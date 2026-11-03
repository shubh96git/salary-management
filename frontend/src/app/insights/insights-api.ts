import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Breakdown, CountryStats, SalaryStats } from './insights.model';

const COUNTRIES = '/api/insights/countries';

@Injectable({ providedIn: 'root' })
export class InsightsApi {
  private readonly http = inject(HttpClient);

  byCountry(): Observable<CountryStats[]> {
    return this.http.get<CountryStats[]>(COUNTRIES);
  }

  breakdown(countryCode: string, by: Breakdown): Observable<SalaryStats[]> {
    return this.http.get<SalaryStats[]>(`${COUNTRIES}/${encodeURIComponent(countryCode)}/${by}`);
  }
}
