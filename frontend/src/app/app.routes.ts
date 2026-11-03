import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'employees' },
  {
    path: 'employees',
    title: 'Employees · Salary Management',
    loadComponent: () => import('./employees/employees-page').then((m) => m.EmployeesPage),
  },
  {
    // The selected country is part of the URL, so a breakdown can be bookmarked or shared.
    path: 'insights/:country',
    title: 'Insights · Salary Management',
    loadComponent: () => import('./insights/insights-page').then((m) => m.InsightsPage),
  },
  {
    path: 'insights',
    title: 'Insights · Salary Management',
    loadComponent: () => import('./insights/insights-page').then((m) => m.InsightsPage),
  },
  { path: '**', redirectTo: 'employees' },
];
