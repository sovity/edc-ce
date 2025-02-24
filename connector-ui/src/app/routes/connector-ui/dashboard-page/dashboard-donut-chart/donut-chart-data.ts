import {ChartConfiguration} from 'chart.js';

export interface DonutChartData {
  labels: string[];
  datasets: ChartConfiguration<'doughnut'>['data']['datasets'];
  options: ChartConfiguration<'doughnut'>['options'];

  isEmpty: boolean;
  emptyMessage: string;

  totalLabel: string;
  totalValue: number;
}
