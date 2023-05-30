import {ClipboardModule} from '@angular/cdk/clipboard';
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatBadgeModule} from '@angular/material/badge';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSelectModule} from '@angular/material/select';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatStepperModule} from '@angular/material/stepper';
import {MatTooltipModule} from '@angular/material/tooltip';
import {RouterModule} from '@angular/router';
import {NgChartsModule} from 'ng2-charts';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {PipesAndDirectivesModule} from '../../../component-library/pipes-and-directives/pipes-and-directives.module';
import {PropertyGridModule} from '../../../component-library/property-grid/property-grid.module';
import {UiElementsModule} from '../../../component-library/ui-elements/ui-elements.module';
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

    // Angular CDK
    ClipboardModule,

    // Angular Material
    MatBadgeModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatStepperModule,
    MatTooltipModule,
    MatPaginatorModule,
    MatInputModule,
    MatIconModule,
    MatDialogModule,

    // Third Party
    NgChartsModule,
    NgxJsonViewerModule,

    // EDC UI Modules
    PipesAndDirectivesModule,
    PropertyGridModule,
    UiElementsModule,
  ],
  declarations: [
    DashboardDonutChartComponent,
    DashboardKpiCardComponent,
    DashboardPageComponent,
  ],
  exports: [DashboardPageComponent],
})
export class DashboardPageModule {}
