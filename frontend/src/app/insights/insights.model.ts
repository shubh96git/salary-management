/** Pay statistics for a group of employees; all amounts are in `currency`. */
export interface SalaryStats {
  group: string;
  currency: string;
  headcount: number;
  minimum: number;
  maximum: number;
  average: number;
  median: number;
  totalPayroll: number;
}

export interface CountryStats extends Omit<SalaryStats, 'group'> {
  countryCode: string;
  countryName: string;
}

export type Breakdown = 'job-titles' | 'departments';
