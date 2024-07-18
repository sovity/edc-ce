import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatBadgeModule} from '@angular/material/badge';
import {MatButtonModule} from '@angular/material/button';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSelectModule} from '@angular/material/select';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatStepperModule} from '@angular/material/stepper';
import {MatTooltipModule} from '@angular/material/tooltip';
import {RouterModule} from '@angular/router';
import {NgChartsModule} from 'ng2-charts';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {CatalogModule} from '../../../component-library/catalog/catalog.module';
import {DataAddressModule} from '../../../component-library/data-address/data-address.module';
import {PipesAndDirectivesModule} from '../../../component-library/pipes-and-directives/pipes-and-directives.module';
import {UiElementsModule} from '../../../component-library/ui-elements/ui-elements.module';
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
    MatProgressBarModule,
    MatInputModule,
    MatIconModule,
    MatDialogModule,

    // Third Party
    NgChartsModule,
    NgxJsonViewerModule,

    // EDC UI Modules
    CatalogModule,
    DataAddressModule,
    PipesAndDirectivesModule,
    UiElementsModule,
    MatButtonToggleModule,
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
