export interface Employee {
  id: number;
  fullName: string;
  email: string;
  jobTitle: string;
  department: string;
  countryCode: string;
  countryName: string;
  currency: string;
  annualSalary: number;
  hireDate: string;
}

/** Body of create and update requests; currency is derived from the country on the server. */
export interface EmployeeRequest {
  fullName: string;
  email: string;
  jobTitle: string;
  department: string;
  countryCode: string;
  annualSalary: number;
  hireDate: string;
}

export type SortDirection = 'asc' | 'desc';

export interface EmployeeQuery {
  search?: string;
  country?: string;
  jobTitle?: string;
  department?: string;
  page: number;
  size: number;
  sort: string;
  direction: SortDirection;
}

export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface CountryOption {
  code: string;
  name: string;
  currency: string;
}

export interface FilterOptions {
  countries: CountryOption[];
  jobTitles: string[];
  departments: string[];
}
