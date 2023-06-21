import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {AutofocusDirective} from './directives/autofocus.direcitive';
import {RemoveClassDirective} from './directives/remove-class.directive';
import {CompareByFieldPipe} from './pipes/compare-by-field.pipe';
import {IsActiveFeaturePipe} from './pipes/is-active-feature.pipe';
import {TrackByFieldPipe} from './pipes/track-by-field.pipe';
import {ValuesPipe} from './pipes/values.pipe';

@NgModule({
  imports: [
    // Angular
    CommonModule,

    // Angular Material
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  declarations: [
    AutofocusDirective,
    CompareByFieldPipe,
    IsActiveFeaturePipe,
    RemoveClassDirective,
    TrackByFieldPipe,
    ValuesPipe,
  ],
  exports: [
    AutofocusDirective,
    CompareByFieldPipe,
    IsActiveFeaturePipe,
    RemoveClassDirective,
    TrackByFieldPipe,
    ValuesPipe,
  ],
})
export class PipesAndDirectivesModule {}
