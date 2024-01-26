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
import {JsonDialogModule} from '../json-dialog/json-dialog.module';
import {PipesAndDirectivesModule} from '../pipes-and-directives/pipes-and-directives.module';
import {PropertyGridModule} from '../property-grid/property-grid.module';
import {UiElementsModule} from '../ui-elements/ui-elements.module';
import {UrlListDialogModule} from '../url-list-dialog/url-list-dialog.module';
import {AssetCardTagListComponent} from './asset-card-tag-list/asset-card-tag-list.component';
import {AssetDetailDialogDataService} from './asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogComponent} from './asset-detail-dialog/asset-detail-dialog.component';
import {AssetDetailDialogService} from './asset-detail-dialog/asset-detail-dialog.service';
import {AssetPropertyGridGroupBuilder} from './asset-detail-dialog/asset-property-grid-group-builder';
import {MarkdownDescriptionComponent} from './asset-detail-dialog/markdown-description/markdown-description.component';
import {PolicyPropertyFieldBuilder} from './asset-detail-dialog/policy-property-field-builder';
import {ContractOfferIconComponent} from './contract-offer-icon/contract-offer-icon.component';
import {ContractOfferMiniListComponent} from './contract-offer-mini-list/contract-offer-mini-list.component';
import {DataOfferCardsComponent} from './data-offer-cards/data-offer-cards.component';
import {IconWithOnlineStatusComponent} from './icon-with-online-status/icon-with-online-status.component';
import {SmallIconWithOnlineStatusText} from './small-icon-with-online-status-text/small-icon-with-online-status-text.component';
import {TransferHistoryMiniListComponent} from './transfer-history-mini-list/transfer-history-mini-list.component';
import {TruncatedShortDescription} from './truncated-short-description/truncated-short-description.component';
import {ViewSelectionComponent} from './view-selection/view-selection.component';

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
    PropertyGridModule,
    UiElementsModule,
    PipesAndDirectivesModule,
  ],
  declarations: [
    AssetDetailDialogComponent,
    ContractOfferIconComponent,
    ContractOfferMiniListComponent,
    DataOfferCardsComponent,
    TransferHistoryMiniListComponent,
    IconWithOnlineStatusComponent,
    MarkdownDescriptionComponent,
    TruncatedShortDescription,
    ViewSelectionComponent,
    SmallIconWithOnlineStatusText,
    AssetCardTagListComponent,
  ],
  exports: [
    AssetDetailDialogComponent,
    ContractOfferIconComponent,
    ContractOfferMiniListComponent,
    DataOfferCardsComponent,
    TransferHistoryMiniListComponent,
    IconWithOnlineStatusComponent,
    TruncatedShortDescription,
    ViewSelectionComponent,
    SmallIconWithOnlineStatusText,
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
