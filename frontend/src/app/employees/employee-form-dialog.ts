import { Component } from '@angular/core';
import { Employee, FilterOptions } from './employee.model';

export interface EmployeeFormData {
  /** Present when editing; absent when adding a new employee. */
  employee?: Employee;
  options: FilterOptions;
}

@Component({
  selector: 'app-employee-form-dialog',
  template: '',
})
export class EmployeeFormDialog {}
