import {Component, HostBinding, Input} from '@angular/core';
import {Fetched} from '../../../../core/services/models/fetched';
import {DonutChartData} from './donut-chart-data';

@Component({
  selector: 'dashboard-donut-chart',
  templateUrl: './dashboard-donut-chart.component.html',
})
export class DashboardDonutChartComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-center')
  @HostBinding('class.items-center')
  @HostBinding('class.items-center')
  @HostBinding('class.min-h-[300px]')
  cls = true;

  @Input()
  data = Fetched.empty<DonutChartData>();
}
