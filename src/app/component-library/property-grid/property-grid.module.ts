import {ClipboardModule} from '@angular/cdk/clipboard';
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatTooltipModule} from '@angular/material/tooltip';
import {PipesAndDirectivesModule} from '../pipes-and-directives/pipes-and-directives.module';
import {UiElementsModule} from '../ui-elements/ui-elements.module';
import {PropertyGridGroupComponent} from './property-grid-group/property-grid-group.component';
import {PropertyGridComponent} from './property-grid/property-grid.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,

    // Angular CDK
    ClipboardModule,

    // Angular Material
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTooltipModule,

    // EDC UI Feature Modules
    PipesAndDirectivesModule,
    UiElementsModule,
  ],
  declarations: [PropertyGridComponent, PropertyGridGroupComponent],
  exports: [PropertyGridComponent, PropertyGridGroupComponent],
})
export class PropertyGridModule {}
