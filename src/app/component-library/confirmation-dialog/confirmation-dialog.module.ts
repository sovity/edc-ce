import {ClipboardModule} from '@angular/cdk/clipboard';
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatDialogModule} from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import {PipesAndDirectivesModule} from '../pipes-and-directives/pipes-and-directives.module';
import {ConfirmationDialogComponent} from './confirmation-dialog/confirmation-dialog.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,

    // Angular CDK
    ClipboardModule,

    // Angular Material
    MatButtonModule,
    MatCheckboxModule,
    MatDialogModule,
    MatIconModule,
    MatTooltipModule,

    // EDC UI Feature Modules
    PipesAndDirectivesModule,
  ],
  declarations: [ConfirmationDialogComponent],
  exports: [ConfirmationDialogComponent],
})
export class ConfirmationDialogModule {}
