import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import {MatDialogModule} from '@angular/material/dialog';
import {MatCardModule} from '@angular/material/card';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatTabsModule} from '@angular/material/tabs';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatListModule} from '@angular/material/list';
import {FlexLayoutModule} from '@angular/flex-layout';
import {CatalogBrowserComponent} from './components/catalog-browser/catalog-browser.component';
import {TransferHistoryViewerComponent} from './components/transfer-history/transfer-history-viewer.component';
import {
  ContractDefinitionViewerComponent
} from './components/contract-definition-viewer/contract-definition-viewer.component';
import {IntroductionComponent} from './components/introduction/introduction.component';
import {RouterModule} from '@angular/router';
import {
  ContractDefinitionEditorDialog
} from './components/contract-definition-editor-dialog/contract-definition-editor-dialog.component';
import {
  CatalogBrowserTransferDialog
} from './components/catalog-browser-transfer-dialog/catalog-browser-transfer-dialog.component';
import {ContractViewerComponent} from './components/contract-viewer/contract-viewer.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {SafePipe} from "./pipes/safe.pipe";
import {ReplacePipe} from "./pipes/replace.pipe";
import {AssetEditorDialog} from "./components/asset-editor-dialog/asset-editor-dialog.component";
import {AssetViewerComponent} from "./components/asset-viewer/asset-viewer.component";

import {PolicyViewComponent} from "./components/policy-view/policy-view.component";
import {PolicyRuleViewerComponent} from "./components/policy-rule-viewer/policy-rule-viewer.component";
import {NewPolicyDialogComponent} from "./components/new-policy-dialog/new-policy-dialog.component";
import {ConfirmationDialogComponent} from './components/confirmation-dialog/confirmation-dialog.component';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {LogoutComponent} from './components/logout/logout.component';
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatStepperModule} from "@angular/material/stepper";
import {IsActiveFeatureSetPipe} from "./pipes/is-active-feature-set.pipe";
import {LanguageSelectComponent} from "./components/language-select/language-select.component";
import {DataCategorySelectComponent} from "./components/data-category-select/data-category-select.component";
import {DataSubcategorySelectComponent} from "./components/data-subcategory-select/data-subcategory-select.component";
import {TransportModeSelectComponent} from "./components/transport-mode-select/transport-mode-select.component";
import {DataSubcategoryItemsPipe} from "./components/data-subcategory-select/data-subcategory-items.pipe";
import {MatChipsModule} from "@angular/material/chips";
import { KeywordSelectComponent } from './components/keyword-select/keyword-select.component';
import {AssetListComponent} from "./components/asset-list/asset-list.component";
import {AssetDetailDialog} from "./components/asset-detail-dialog/asset-detail-dialog.component";
import {ContractOfferListComponent} from "./components/contract-offer-list/contract-offer-list.component";

@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
        FormsModule,
        ReactiveFormsModule,
        MatInputModule,
        MatFormFieldModule,
        MatSelectModule,
        MatButtonModule,
        MatDialogModule,
        MatCardModule,
        MatGridListModule,
        FlexLayoutModule,
        MatExpansionModule,
        MatTableModule,
        MatPaginatorModule,
        MatIconModule,
        MatDividerModule,
        MatSlideToggleModule,
        MatTabsModule,
        MatProgressBarModule,
        MatListModule,
        RouterModule,
        MatProgressSpinnerModule,
        MatDatepickerModule,
        MatTooltipModule,
        MatStepperModule,
        MatChipsModule,
    ],
  declarations: [
    CatalogBrowserComponent,
    TransferHistoryViewerComponent,
    ContractDefinitionViewerComponent,
    AssetViewerComponent,
    AssetEditorDialog,
    IntroductionComponent,
    ContractDefinitionEditorDialog,
    CatalogBrowserTransferDialog,
    ContractViewerComponent,
    CatalogBrowserTransferDialog,
    SafePipe,
    ReplacePipe,
    PolicyViewComponent,
    PolicyRuleViewerComponent,
    CatalogBrowserTransferDialog,
    ContractViewerComponent,
    NewPolicyDialogComponent,
    IntroductionComponent,
    ConfirmationDialogComponent,
    LogoutComponent,
    IsActiveFeatureSetPipe,
    LanguageSelectComponent,
    DataCategorySelectComponent,
    DataSubcategorySelectComponent,
    DataSubcategoryItemsPipe,
    TransportModeSelectComponent,
    KeywordSelectComponent,
    AssetListComponent,
    AssetDetailDialog,
    ContractOfferListComponent
  ],
  exports: [
    CatalogBrowserComponent,
    TransferHistoryViewerComponent,
    ContractDefinitionViewerComponent,
    AssetViewerComponent,
    IntroductionComponent,
    PolicyRuleViewerComponent,
    IntroductionComponent,
    NewPolicyDialogComponent,
    ContractViewerComponent,
    IsActiveFeatureSetPipe
  ]
})
export class EdcDemoModule {
}
