import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../../../shared/shared.module';
import {DashboardDonutChartComponent} from './dashboard-donut-chart/dashboard-donut-chart.component';
import {DashboardKpiCardComponent} from './dashboard-kpi-card/dashboard-kpi-card.component';
import {DashboardPageComponent} from './dashboard-page/dashboard-page.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

    // EDC UI Modules
    SharedModule,
  ],
  declarations: [
    DashboardDonutChartComponent,
    DashboardKpiCardComponent,
    DashboardPageComponent,
  ],
  exports: [DashboardPageComponent],
})
export class DashboardPageModule {}
