import { Component } from '@angular/core';

export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmLabel: string;
}

@Component({
  selector: 'app-confirm-dialog',
  template: '',
})
export class ConfirmDialog {}
