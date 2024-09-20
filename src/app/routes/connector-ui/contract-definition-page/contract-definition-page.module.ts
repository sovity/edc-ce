import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../../../shared/shared.module';
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

    // EDC UI Modules
    SharedModule,
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
