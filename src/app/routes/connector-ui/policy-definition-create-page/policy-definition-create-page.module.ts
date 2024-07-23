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
import {MatDividerModule} from '@angular/material/divider';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatMenuModule} from '@angular/material/menu';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSelectModule} from '@angular/material/select';
import {MatTooltipModule} from '@angular/material/tooltip';
import {RouterModule} from '@angular/router';
import {JsonDialogModule} from '../../../component-library/json-dialog/json-dialog.module';
import {PipesAndDirectivesModule} from '../../../component-library/pipes-and-directives/pipes-and-directives.module';
import {PolicyEditorModule} from '../../../component-library/policy-editor/policy-editor.module';
import {UiElementsModule} from '../../../component-library/ui-elements/ui-elements.module';
import {PolicyDefinitionCreatePageComponent} from './policy-definition-create-page/policy-definition-create-page.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

    // Angular Material
    MatBadgeModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
    MatDividerModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatMenuModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatTooltipModule,
    MatPaginatorModule,
    MatInputModule,
    MatIconModule,
    MatDialogModule,

    // EDC UI Modules
    PolicyEditorModule,
    UiElementsModule,
    JsonDialogModule,
    PipesAndDirectivesModule,
  ],
  declarations: [PolicyDefinitionCreatePageComponent],
  exports: [PolicyDefinitionCreatePageComponent],
})
export class PolicyDefinitionCreatePageModule {}
