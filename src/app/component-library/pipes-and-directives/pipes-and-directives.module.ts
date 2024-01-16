import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {AutofocusDirective} from './directives/autofocus.direcitive';
import {ExternalLinkDirective} from './directives/external-link.directive';
import {RemoveClassDirective} from './directives/remove-class.directive';
import {TrackByFieldDirective} from './directives/track-by-field.directive';
import {CompareByFieldPipe} from './pipes/compare-by-field.pipe';
import {IsActiveFeaturePipe} from './pipes/is-active-feature.pipe';
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
    ExternalLinkDirective,
    IsActiveFeaturePipe,
    RemoveClassDirective,
    TrackByFieldDirective,
    ValuesPipe,
  ],
  exports: [
    AutofocusDirective,
    CompareByFieldPipe,
    ExternalLinkDirective,
    IsActiveFeaturePipe,
    RemoveClassDirective,
    TrackByFieldDirective,
    ValuesPipe,
  ],
})
export class PipesAndDirectivesModule {}
