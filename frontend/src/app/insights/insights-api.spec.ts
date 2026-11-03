import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { InsightsApi } from './insights-api';

describe('InsightsApi', () => {
  let api: InsightsApi;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    api = TestBed.inject(InsightsApi);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads pay statistics per country', () => {
    api.byCountry().subscribe();

    expect(http.expectOne('/api/insights/countries').request.method).toBe('GET');
  });

  it('loads the job title breakdown for a country', () => {
    api.breakdown('IN', 'job-titles').subscribe();

    expect(http.expectOne('/api/insights/countries/IN/job-titles').request.method).toBe('GET');
  });

  it('loads the department breakdown for a country', () => {
    api.breakdown('US', 'departments').subscribe();

    expect(http.expectOne('/api/insights/countries/US/departments').request.method).toBe('GET');
  });
});
