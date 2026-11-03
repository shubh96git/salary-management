import { Component, input } from '@angular/core';

@Component({
  selector: 'app-insights-page',
  template: '',
})
export class InsightsPage {
  /** ISO country code from the route (/insights/:country), if one is selected. */
  readonly country = input<string>();
}
