import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../../../shared/shared.module';
import {AssetCardsComponent} from './asset-cards/asset-cards.component';
import {AssetCreateDialogComponent} from './asset-create-dialog/asset-create-dialog.component';
import {AssetCreateDialogService} from './asset-create-dialog/asset-create-dialog.service';
import {AssetCreateDialogFormMapper} from './asset-create-dialog/form/asset-create-dialog-form-mapper';
import {AssetListPageComponent} from './asset-list-page/asset-list-page.component';

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
    AssetCardsComponent,
    AssetListPageComponent,
    AssetCreateDialogComponent,
  ],
  providers: [AssetCreateDialogService, AssetCreateDialogFormMapper],
  exports: [AssetListPageComponent],
})
export class AssetListPageModule {}
