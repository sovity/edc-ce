import {ChartConfiguration} from 'chart.js';

export interface DonutChartData {
  labels: string[];
  datasets: ChartConfiguration<'doughnut'>['data']['datasets'];
  options: ChartConfiguration<'doughnut'>['options'];
}
