import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatDialogModule} from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {ConfirmationDialogModule} from '../confirmation-dialog/confirmation-dialog.module';
import {PipesAndDirectivesModule} from '../pipes-and-directives/pipes-and-directives.module';
import {MarkdownDescriptionComponent} from './markdown-description.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    FormsModule,

    // Angular Material
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatDialogModule,
    MatIconModule,
    MatTooltipModule,

    // Third Party
    NgxJsonViewerModule,

    // EDC UI Modules
    PipesAndDirectivesModule,
    ConfirmationDialogModule,
  ],
  declarations: [MarkdownDescriptionComponent],
  providers: [],
  exports: [MarkdownDescriptionComponent],
})
export class MarkdownDescriptionModule {}
