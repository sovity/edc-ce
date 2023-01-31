import {Component, HostBinding, Input} from '@angular/core';
import {Fetched} from '../../models/fetched';

@Component({
  selector: 'edc-demo-dashboard-kpi-card',
  styles: [
    `
      :host {
        min-width: 200px;
        height: 180px;
      }
    `,
  ],
  templateUrl: './dashboard-kpi-card.component.html',
})
export class DashboardKpiCardComponent {
  @HostBinding('class.flex')
  cls = true;

  @Input()
  kpi = Fetched.empty<number>();

  @Input()
  label = 'Label';

  fontSize(data: number): number {
    const abs = Math.abs(data);
    if (abs < 100) {
      return 81;
    } else if (abs < 1000) {
      return 72;
    } else if (abs < 10000) {
      return 64;
    } else {
      return 56;
    }
  }
}
