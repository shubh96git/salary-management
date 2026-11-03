import { Employee, FilterOptions, Page } from '../employee.model';

export function anEmployee(overrides: Partial<Employee> = {}): Employee {
  return {
    id: 1,
    fullName: 'Asha Rao',
    email: 'asha.rao@acme.example',
    jobTitle: 'Software Engineer',
    department: 'Engineering',
    countryCode: 'IN',
    countryName: 'India',
    currency: 'INR',
    annualSalary: 1500000,
    hireDate: '2021-04-01',
    ...overrides,
  };
}

export function aPageOf(content: Employee[], totalElements = content.length): Page<Employee> {
  return { content, page: 0, size: 20, totalElements, totalPages: Math.ceil(totalElements / 20) };
}

export const FILTER_OPTIONS: FilterOptions = {
  countries: [
    { code: 'IN', name: 'India', currency: 'INR' },
    { code: 'US', name: 'United States', currency: 'USD' },
  ],
  jobTitles: ['Product Manager', 'Software Engineer'],
  departments: ['Engineering', 'Product'],
};
