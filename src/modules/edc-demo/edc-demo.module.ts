import {ClipboardModule} from '@angular/cdk/clipboard';
import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FlexLayoutModule} from '@angular/flex-layout';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatBadgeModule} from '@angular/material/badge';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatDividerModule} from '@angular/material/divider';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSelectModule} from '@angular/material/select';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatStepperModule} from '@angular/material/stepper';
import {MatTableModule} from '@angular/material/table';
import {MatTabsModule} from '@angular/material/tabs';
import {MatTooltipModule} from '@angular/material/tooltip';
import {RouterModule} from '@angular/router';
import {NgChartsModule} from 'ng2-charts';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {AssetDetailDialog} from './components/asset-detail-dialog/asset-detail-dialog.component';
import {AssetEditorDialog} from './components/asset-editor-dialog/asset-editor-dialog.component';
import {AssetListComponent} from './components/asset-list/asset-list.component';
import {AssetViewerComponent} from './components/asset-viewer/asset-viewer.component';
import {CatalogBrowserFetchDetailDialogComponent} from './components/catalog-browser-fetch-detail-dialog/catalog-browser-fetch-detail-dialog.component';
import {CatalogBrowserComponent} from './components/catalog-browser/catalog-browser.component';
import {ConfirmationDialogComponent} from './components/confirmation-dialog/confirmation-dialog.component';
import {ContractAgreementTransferDialog} from './components/contract-agreement-transfer-dialog/contract-agreement-transfer-dialog.component';
import {ContractDefinitionCardsComponent} from './components/contract-definition-cards/contract-definition-cards.component';
import {ContractDefinitionEditorDialog} from './components/contract-definition-editor-dialog/contract-definition-editor-dialog.component';
import {ContractDefinitionPageComponent} from './components/contract-definition-page/contract-definition-page.component';
import {ContractOfferIconComponent} from './components/contract-offer-icon/contract-offer-icon.component';
import {ContractOfferListComponent} from './components/contract-offer-list/contract-offer-list.component';
import {ContractViewerComponent} from './components/contract-viewer/contract-viewer.component';
import {DashboardDonutChartComponent} from './components/dashboard-donut-chart/dashboard-donut-chart.component';
import {DashboardKpiCardComponent} from './components/dashboard-kpi-card/dashboard-kpi-card.component';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {DataAddressTypeSelectComponent} from './components/data-address-type-select/data-address-type-select.component';
import {DataCategorySelectComponent} from './components/data-category-select/data-category-select.component';
import {DataSubcategoryItemsPipe} from './components/data-subcategory-select/data-subcategory-items.pipe';
import {DataSubcategorySelectComponent} from './components/data-subcategory-select/data-subcategory-select.component';
import {EmptyStateComponent} from './components/empty-state/empty-state.component';
import {ErrorStateComponent} from './components/error-state/error-state.component';
import {JsonDialogComponent} from './components/json-dialog/json-dialog.component';
import {KeywordSelectComponent} from './components/keyword-select/keyword-select.component';
import {LanguageSelectComponent} from './components/language-select/language-select.component';
import {LoadingStateComponent} from './components/loading-state/loading-state.component';
import {LogoutComponent} from './components/logout/logout.component';
import {NewPolicyDialogComponent} from './components/new-policy-dialog/new-policy-dialog.component';
import {PolicyCardsComponent} from './components/policy-cards/policy-cards.component';
import {PolicyRuleViewerComponent} from './components/policy-rule-viewer/policy-rule-viewer.component';
import {PolicySelectComponent} from './components/policy-select/policy-select.component';
import {PolicyViewComponent} from './components/policy-view/policy-view.component';
import {TransferHistoryViewerComponent} from './components/transfer-history/transfer-history-viewer.component';
import {TransportModeSelectComponent} from './components/transport-mode-select/transport-mode-select.component';
import {IsActiveFeaturePipe} from './pipes/is-active-feature.pipe';
import {ReplacePipe} from './pipes/replace.pipe';
import {SafePipe} from './pipes/safe.pipe';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

    // Angular CDK
    ClipboardModule,

    // Angular Material
    MatBadgeModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
    MatDatepickerModule,
    MatDialogModule,
    MatDividerModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatStepperModule,
    MatTableModule,
    MatTabsModule,
    MatTooltipModule,

    // Angular Flex Layout (deprecated)
    FlexLayoutModule,

    // Third Party
    NgChartsModule,
    NgxJsonViewerModule,
  ],
  declarations: [
    AssetDetailDialog,
    AssetEditorDialog,
    AssetListComponent,
    AssetViewerComponent,
    CatalogBrowserComponent,
    CatalogBrowserFetchDetailDialogComponent,
    ConfirmationDialogComponent,
    ContractAgreementTransferDialog,
    ContractDefinitionCardsComponent,
    ContractDefinitionEditorDialog,
    ContractDefinitionPageComponent,
    ContractOfferIconComponent,
    ContractOfferListComponent,
    ContractViewerComponent,
    ContractViewerComponent,
    DashboardComponent,
    DashboardDonutChartComponent,
    DashboardKpiCardComponent,
    DataAddressTypeSelectComponent,
    DataCategorySelectComponent,
    DataSubcategoryItemsPipe,
    DataSubcategorySelectComponent,
    EmptyStateComponent,
    ErrorStateComponent,
    IsActiveFeaturePipe,
    JsonDialogComponent,
    KeywordSelectComponent,
    LanguageSelectComponent,
    LoadingStateComponent,
    LogoutComponent,
    NewPolicyDialogComponent,
    PolicyCardsComponent,
    PolicyRuleViewerComponent,
    PolicySelectComponent,
    PolicyViewComponent,
    ReplacePipe,
    SafePipe,
    TransferHistoryViewerComponent,
    TransportModeSelectComponent,
  ],
  exports: [
    AssetViewerComponent,
    CatalogBrowserComponent,
    ContractDefinitionPageComponent,
    ContractViewerComponent,
    IsActiveFeaturePipe,
    NewPolicyDialogComponent,
    PolicyRuleViewerComponent,
    TransferHistoryViewerComponent,
  ],
})
export class EdcDemoModule {}
