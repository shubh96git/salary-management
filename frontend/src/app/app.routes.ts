import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'employees' },
  {
    path: 'employees',
    title: 'Employees · Salary Management',
    loadComponent: () => import('./employees/employees-page').then((m) => m.EmployeesPage),
  },
  { path: '**', redirectTo: 'employees' },
];
