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
import {PipesAndDirectivesModule} from '../pipes-and-directives/pipes-and-directives.module';
import {PropertyGridModule} from '../property-grid/property-grid.module';
import {UiElementsModule} from '../ui-elements/ui-elements.module';
import {AssetDetailDialogDataService} from './asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogComponent} from './asset-detail-dialog/asset-detail-dialog.component';
import {AssetPropertyGridGroupBuilder} from './asset-detail-dialog/asset-property-grid-group-builder';
import {ContractOfferCardsComponent} from './contract-offer-cards/contract-offer-cards.component';
import {ContractOfferIconComponent} from './contract-offer-icon/contract-offer-icon.component';
import {TransferHistoryMiniListComponent} from './transfer-history-mini-list/transfer-history-mini-list.component';

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

    // Features
    PropertyGridModule,
    UiElementsModule,
    PipesAndDirectivesModule,
  ],
  declarations: [
    AssetDetailDialogComponent,
    ContractOfferCardsComponent,
    ContractOfferIconComponent,
    TransferHistoryMiniListComponent,
  ],
  exports: [
    AssetDetailDialogComponent,
    ContractOfferCardsComponent,
    ContractOfferIconComponent,
    TransferHistoryMiniListComponent,
  ],
  providers: [AssetPropertyGridGroupBuilder, AssetDetailDialogDataService],
})
export class CatalogModule {}
