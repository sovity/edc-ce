import {ClipboardModule} from '@angular/cdk/clipboard';
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatBadgeModule} from '@angular/material/badge';
import {MatButtonModule} from '@angular/material/button';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {
  DateAdapter,
  MAT_DATE_LOCALE,
  MatNativeDateModule,
} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatDividerModule} from '@angular/material/divider';
import {MatExpansionModule} from '@angular/material/expansion';
import {
  MAT_FORM_FIELD_DEFAULT_OPTIONS,
  MatFormFieldDefaultOptions,
  MatFormFieldModule,
} from '@angular/material/form-field';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {MatMenuModule} from '@angular/material/menu';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatRadioModule} from '@angular/material/radio';
import {MatSelectModule} from '@angular/material/select';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatStepperModule} from '@angular/material/stepper';
import {MatTableModule} from '@angular/material/table';
import {MatTabsModule} from '@angular/material/tabs';
import {MatToolbarModule} from '@angular/material/toolbar';
import {
  MAT_TOOLTIP_DEFAULT_OPTIONS,
  MAT_TOOLTIP_DEFAULT_OPTIONS_FACTORY,
  MatTooltipDefaultOptions,
  MatTooltipModule,
} from '@angular/material/tooltip';
import {TranslateModule} from '@ngx-translate/core';
import {NgChartsModule} from 'ng2-charts';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {CustomDateAdapter} from '../core/adapters/custom-date-adapter';
import {AssetCardTagListComponent} from './business/asset-card-tag-list/asset-card-tag-list.component';
import {AssetDetailDialogDataService} from './business/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogComponent} from './business/asset-detail-dialog/asset-detail-dialog.component';
import {AssetDetailDialogService} from './business/asset-detail-dialog/asset-detail-dialog.service';
import {AssetPropertyGridGroupBuilder} from './business/asset-detail-dialog/asset-property-grid-group-builder';
import {PolicyPropertyFieldBuilder} from './business/asset-detail-dialog/policy-property-field-builder';
import {ConditionsForUseDialogComponent} from './business/conditions-for-use-dialog/conditions-for-use-dialog.component';
import {ConditionsForUseDialogService} from './business/conditions-for-use-dialog/conditions-for-use-dialog.service';
import {ContractOfferIconComponent} from './business/contract-offer-icon/contract-offer-icon.component';
import {ContractOfferMiniListComponent} from './business/contract-offer-mini-list/contract-offer-mini-list.component';
import {DataOfferCardsComponent} from './business/data-offer-cards/data-offer-cards.component';
import {EditAssetFormComponent} from './business/edit-asset-form/edit-asset-form.component';
import {InitiateNegotiationConfirmTosDialogComponent} from './business/initiate-negotiation-confirm-tos-dialog/initiate-negotiation-confirm-tos-dialog.component';
import {PolicyFormAddMenuComponent} from './business/policy-editor/editor/policy-form-add-menu/policy-form-add-menu.component';
import {PolicyFormExpressionConstraintComponent} from './business/policy-editor/editor/policy-form-expression-constraint/policy-form-expression-constraint.component';
import {PolicyFormExpressionEmptyComponent} from './business/policy-editor/editor/policy-form-expression-empty/policy-form-expression-empty.component';
import {PolicyFormExpressionMultiComponent} from './business/policy-editor/editor/policy-form-expression-multi/policy-form-expression-multi.component';
import {PolicyFormExpressionComponent} from './business/policy-editor/editor/policy-form-expression/policy-form-expression.component';
import {PolicyFormRemoveButton} from './business/policy-editor/editor/policy-form-remove-button/policy-form-remove-button.component';
import {PolicyExpressionRecipeService} from './business/policy-editor/editor/recipes/policy-expression-recipe.service';
import {TimespanRestrictionDialogComponent} from './business/policy-editor/editor/recipes/timespan-restriction-dialog/timespan-restriction-dialog.component';
import {PolicyMapper} from './business/policy-editor/model/policy-mapper';
import {PolicyMultiExpressionService} from './business/policy-editor/model/policy-multi-expressions';
import {PolicyOperatorService} from './business/policy-editor/model/policy-operators';
import {PolicyVerbService} from './business/policy-editor/model/policy-verbs';
import {PolicyExpressionComponent} from './business/policy-editor/renderer/policy-expression/policy-expression.component';
import {PolicyRendererComponent} from './business/policy-editor/renderer/policy-renderer/policy-renderer.component';
import {TransferHistoryMiniListComponent} from './business/transfer-history-mini-list/transfer-history-mini-list.component';
import {AgoComponent} from './common/ago/ago.component';
import {AgoPipe} from './common/ago/ago.pipe';
import {ConfirmationDialogComponent} from './common/confirmation-dialog/confirmation-dialog.component';
import {DateComponent} from './common/date/date.component';
import {EmptyStateComponent} from './common/empty-state/empty-state.component';
import {ErrorStateComponent} from './common/error-state/error-state.component';
import {HorizontalSectionDividerComponent} from './common/horizontal-section-divider/horizontal-section-divider.component';
import {JsonDialogComponent} from './common/json-dialog/json-dialog.component';
import {JsonDialogService} from './common/json-dialog/json-dialog.service';
import {LanguageSelectorComponent} from './common/language-selector/language-selector.component';
import {LoadingStateComponent} from './common/loading-state/loading-state.component';
import {MarkdownDescriptionComponent} from './common/markdown-description/markdown-description.component';
import {PropertyGridGroupComponent} from './common/property-grid-group/property-grid-group.component';
import {PropertyGridComponent} from './common/property-grid/property-grid.component';
import {TranslateWithSlotComponent} from './common/translate-with-slot/translate-with-slot.component';
import {TruncatedShortDescription} from './common/truncated-short-description/truncated-short-description.component';
import {UrlListDialogComponent} from './common/url-list-dialog/url-list-dialog.component';
import {UrlListDialogService} from './common/url-list-dialog/url-list-dialog.service';
import {DataAddressTypeSelectComponent} from './form-elements/data-address-type-select/data-address-type-select.component';
import {DataCategorySelectComponent} from './form-elements/data-category-select/data-category-select.component';
import {DataSubcategoryItemsPipe} from './form-elements/data-subcategory-select/data-subcategory-items.pipe';
import {DataSubcategorySelectComponent} from './form-elements/data-subcategory-select/data-subcategory-select.component';
import {EditAssetFormDataAddressTypeSelectComponent} from './form-elements/edit-asset-form-data-address-type-select/edit-asset-form-data-address-type-select.component';
import {EditAssetFormGroupComponent} from './form-elements/edit-asset-form-group/edit-asset-form-group.component';
import {EditAssetFormInputComponent} from './form-elements/edit-asset-form-input/edit-asset-form-input.component';
import {EditAssetFormLabelComponent} from './form-elements/edit-asset-form-label/edit-asset-form-label.component';
import {EditAssetFormTextareaComponent} from './form-elements/edit-asset-form-textarea/edit-asset-form-textarea.component';
import {KeywordSelectComponent} from './form-elements/keyword-select/keyword-select.component';
import {LanguageSelectComponent} from './form-elements/language-select/language-select.component';
import {ParticipantIdSelectComponent} from './form-elements/participant-id-select/participant-id-select.component';
import {PolicyOperatorSelectComponent} from './form-elements/policy-operator-select/policy-operator-select.component';
import {TransportModeSelectComponent} from './form-elements/transport-mode-select/transport-mode-select.component';
import {AutofocusDirective} from './pipes-and-directives/autofocus.direcitive';
import {CompareByFieldPipe} from './pipes-and-directives/compare-by-field.pipe';
import {ExternalLinkDirective} from './pipes-and-directives/external-link.directive';
import {IsActiveFeaturePipe} from './pipes-and-directives/is-active-feature.pipe';
import {RemoveClassDirective} from './pipes-and-directives/remove-class.directive';
import {TrackByFieldDirective} from './pipes-and-directives/track-by-field.directive';
import {ValuesPipe} from './pipes-and-directives/values.pipe';

const COMPONENTS: NgModule['declarations'] = [
  // ./common
  AgoComponent,
  AgoPipe,
  ConfirmationDialogComponent,
  DateComponent,
  EmptyStateComponent,
  ErrorStateComponent,
  HorizontalSectionDividerComponent,
  JsonDialogComponent,
  LanguageSelectorComponent,
  LoadingStateComponent,
  MarkdownDescriptionComponent,
  PropertyGridComponent,
  PropertyGridGroupComponent,
  TruncatedShortDescription,
  UrlListDialogComponent,
  TranslateWithSlotComponent,

  // ./business
  AssetCardTagListComponent,
  AssetDetailDialogComponent,
  ConditionsForUseDialogComponent,
  ContractOfferIconComponent,
  ContractOfferMiniListComponent,
  DataOfferCardsComponent,
  InitiateNegotiationConfirmTosDialogComponent,
  TransferHistoryMiniListComponent,

  // ./business/asset-edit-form
  EditAssetFormComponent,
  EditAssetFormGroupComponent,
  EditAssetFormLabelComponent,
  EditAssetFormInputComponent,
  EditAssetFormTextareaComponent,
  DataSubcategorySelectComponent,
  DataSubcategoryItemsPipe,

  // ./business/policy-editor
  PolicyFormAddMenuComponent,
  PolicyFormExpressionComponent,
  PolicyFormExpressionEmptyComponent,
  PolicyFormExpressionConstraintComponent,
  PolicyFormExpressionMultiComponent,
  PolicyFormRemoveButton,
  TimespanRestrictionDialogComponent,
  PolicyRendererComponent,
  PolicyExpressionComponent,

  // ./form-elements
  DataAddressTypeSelectComponent,
  DataCategorySelectComponent,
  DataSubcategorySelectComponent,
  DataSubcategoryItemsPipe,
  EditAssetFormDataAddressTypeSelectComponent,
  EditAssetFormGroupComponent,
  EditAssetFormInputComponent,
  EditAssetFormLabelComponent,
  EditAssetFormTextareaComponent,
  KeywordSelectComponent,
  LanguageSelectorComponent,
  LanguageSelectComponent,
  ParticipantIdSelectComponent,
  PolicyOperatorSelectComponent,
  TransportModeSelectComponent,

  // ./pipes-and-directives
  AutofocusDirective,
  CompareByFieldPipe,
  ExternalLinkDirective,
  IsActiveFeaturePipe,
  RemoveClassDirective,
  TrackByFieldDirective,
  ValuesPipe,
];

const MODULES = [
  // Angular
  TranslateModule,

  // Angular CDK
  ClipboardModule,

  // Angular Material
  MatBadgeModule,
  MatButtonModule,
  MatButtonToggleModule,
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
  MatMenuModule,
  MatNativeDateModule,
  MatPaginatorModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
  MatRadioModule,
  MatSelectModule,
  MatSidenavModule,
  MatSlideToggleModule,
  MatSnackBarModule,
  MatStepperModule,
  MatTableModule,
  MatTabsModule,
  MatToolbarModule,
  MatTooltipModule,

  // NGX Json Viewer
  NgxJsonViewerModule,

  // NgCharts
  NgChartsModule,
];

@NgModule({
  imports: [
    // Angular
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    // Angular Material
    ...MODULES,
  ],
  exports: [...MODULES, ...COMPONENTS],
  declarations: COMPONENTS,
  providers: [
    AssetDetailDialogDataService,
    AssetDetailDialogService,
    AssetPropertyGridGroupBuilder,
    ConditionsForUseDialogService,
    PolicyPropertyFieldBuilder,
    JsonDialogService,
    PolicyExpressionRecipeService,
    UrlListDialogService,
    PolicyMultiExpressionService,
    PolicyOperatorService,
    PolicyVerbService,
    PolicyMapper,

    {provide: DateAdapter, useClass: CustomDateAdapter},

    {provide: MAT_DATE_LOCALE, useValue: 'en-GB'},
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: {
        appearance: 'outline',
        color: 'accent',
      } as MatFormFieldDefaultOptions,
    },
    {
      provide: MAT_TOOLTIP_DEFAULT_OPTIONS,
      useValue: <MatTooltipDefaultOptions>{
        ...MAT_TOOLTIP_DEFAULT_OPTIONS_FACTORY(),
        disableTooltipInteractivity: true,
      },
    },
  ],
})
export class SharedModule {}
