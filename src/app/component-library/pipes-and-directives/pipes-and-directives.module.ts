import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {RemoveClassDirective} from './directives/remove-class.directive';
import {IsActiveFeaturePipe} from './pipes/is-active-feature.pipe';

@NgModule({
  imports: [
    // Angular
    CommonModule,

    // Angular Material
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  declarations: [IsActiveFeaturePipe, RemoveClassDirective],
  exports: [IsActiveFeaturePipe, RemoveClassDirective],
})
export class PipesAndDirectivesModule {}
