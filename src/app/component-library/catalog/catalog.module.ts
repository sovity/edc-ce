import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatChipsModule} from '@angular/material/chips';
import {MatDialogModule} from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatTooltipModule} from '@angular/material/tooltip';
import {ConditionsForUseDialogModule} from '../conditions-for-use-dialog/conditions-for-use-dialog.module';
import {InitiateNegotiationConfirmTosDialogModule} from '../initiate-negotiation-confirm-tos-dialog/initiate-negotiation-confirm-tos-dialog.module';
import {JsonDialogModule} from '../json-dialog/json-dialog.module';
import {MarkdownDescriptionModule} from '../markdown-description/markdown-description.module';
import {PipesAndDirectivesModule} from '../pipes-and-directives/pipes-and-directives.module';
import {PropertyGridModule} from '../property-grid/property-grid.module';
import {UiElementsModule} from '../ui-elements/ui-elements.module';
import {UrlListDialogModule} from '../url-list-dialog/url-list-dialog.module';
import {AssetCardTagListComponent} from './asset-card-tag-list/asset-card-tag-list.component';
import {AssetDetailDialogDataService} from './asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogComponent} from './asset-detail-dialog/asset-detail-dialog.component';
import {AssetDetailDialogService} from './asset-detail-dialog/asset-detail-dialog.service';
import {AssetPropertyGridGroupBuilder} from './asset-detail-dialog/asset-property-grid-group-builder';
import {PolicyPropertyFieldBuilder} from './asset-detail-dialog/policy-property-field-builder';
import {ContractOfferIconComponent} from './contract-offer-icon/contract-offer-icon.component';
import {ContractOfferMiniListComponent} from './contract-offer-mini-list/contract-offer-mini-list.component';
import {DataOfferCardsComponent} from './data-offer-cards/data-offer-cards.component';
import {TransferHistoryMiniListComponent} from './transfer-history-mini-list/transfer-history-mini-list.component';
import {TruncatedShortDescription} from './truncated-short-description/truncated-short-description.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,

    // Angular Material
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatTooltipModule,
    MatIconModule,
    MatChipsModule,

    // Features
    JsonDialogModule,
    UrlListDialogModule,
    ConditionsForUseDialogModule,
    PropertyGridModule,
    UiElementsModule,
    PipesAndDirectivesModule,
    MarkdownDescriptionModule,

    InitiateNegotiationConfirmTosDialogModule,
  ],
  declarations: [
    AssetDetailDialogComponent,
    ContractOfferIconComponent,
    ContractOfferMiniListComponent,
    DataOfferCardsComponent,
    TransferHistoryMiniListComponent,
    TruncatedShortDescription,
    AssetCardTagListComponent,
  ],
  exports: [
    AssetDetailDialogComponent,
    ContractOfferIconComponent,
    ContractOfferMiniListComponent,
    DataOfferCardsComponent,
    TransferHistoryMiniListComponent,
    TruncatedShortDescription,
    AssetCardTagListComponent,
  ],
  providers: [
    AssetPropertyGridGroupBuilder,
    AssetDetailDialogDataService,
    AssetDetailDialogService,
    PolicyPropertyFieldBuilder,
  ],
})
export class CatalogModule {}
