import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../../../shared/shared.module';
import {ContractAgreementCardsComponent} from './contract-agreement-cards/contract-agreement-cards.component';
import {ContractAgreementPageComponent} from './contract-agreement-page/contract-agreement-page.component';
import {ContractAgreementTerminationDialogComponent} from './contract-agreement-termination-dialog/contract-agreement-termination-dialog.component';
import {ContractAgreementTransferDialogComponent} from './contract-agreement-transfer-dialog/contract-agreement-transfer-dialog.component';

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
    ContractAgreementPageComponent,
    ContractAgreementCardsComponent,
    ContractAgreementTerminationDialogComponent,
    ContractAgreementTransferDialogComponent,
  ],
  exports: [ContractAgreementPageComponent],
})
export class ContractAgreementPageModule {}
