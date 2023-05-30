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
import {CatalogModule} from '../../../component-library/catalog/catalog.module';
import {JsonDialogModule} from '../../../component-library/json-dialog/json-dialog.module';
import {PipesAndDirectivesModule} from '../../../component-library/pipes-and-directives/pipes-and-directives.module';
import {UiElementsModule} from '../../../component-library/ui-elements/ui-elements.module';
import {AssetSelectComponent} from './asset-select/asset-select.component';
import {ContractDefinitionCardsComponent} from './contract-definition-cards/contract-definition-cards.component';
import {ContractDefinitionEditorDialog} from './contract-definition-editor-dialog/contract-definition-editor-dialog.component';
import {ContractDefinitionPageComponent} from './contract-definition-page/contract-definition-page.component';
import {PolicySelectComponent} from './policy-select/policy-select.component';

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

    // EDC UI Modules
    CatalogModule,
    JsonDialogModule,
    PipesAndDirectivesModule,
    UiElementsModule,
  ],
  declarations: [
    AssetSelectComponent,
    ContractDefinitionCardsComponent,
    ContractDefinitionEditorDialog,
    ContractDefinitionPageComponent,
    PolicySelectComponent,
  ],
  exports: [ContractDefinitionPageComponent],
})
export class ContractDefinitionPageModule {}
