import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatTooltipModule} from '@angular/material/tooltip';
import {AgoComponent} from './ago/ago.component';
import {AgoPipe} from './ago/ago.pipe';
import {DateComponent} from './date/date.component';
import {EmptyStateComponent} from './empty-state/empty-state.component';
import {ErrorStateComponent} from './error-state/error-state.component';
import {HorizontalSectionDividerComponent} from './horizontal-section-divider/horizontal-section-divider.component';
import {LoadingStateComponent} from './loading-state/loading-state.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,

    // Angular Material
    MatIconModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
  ],
  declarations: [
    AgoComponent,
    AgoPipe,
    DateComponent,
    EmptyStateComponent,
    ErrorStateComponent,
    HorizontalSectionDividerComponent,
    LoadingStateComponent,
  ],
  exports: [
    AgoComponent,
    AgoPipe,
    DateComponent,
    EmptyStateComponent,
    ErrorStateComponent,
    HorizontalSectionDividerComponent,
    LoadingStateComponent,
  ],
})
export class UiElementsModule {}
