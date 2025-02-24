import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../../../shared/shared.module';
import {CatalogBrowserFetchDetailDialogComponent} from './catalog-browser-fetch-detail-dialog/catalog-browser-fetch-detail-dialog.component';
import {CatalogBrowserPageService} from './catalog-browser-page/catalog-browser-page-service';
import {CatalogBrowserPageComponent} from './catalog-browser-page/catalog-browser-page.component';
import {DataOfferBuilder} from './catalog-browser-page/data-offer-builder';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

    // EDC UI Modules
    SharedModule,
  ],
  declarations: [
    CatalogBrowserPageComponent,
    CatalogBrowserFetchDetailDialogComponent,
  ],
  exports: [CatalogBrowserPageComponent],
  providers: [CatalogBrowserPageService, DataOfferBuilder, DataOfferBuilder],
})
export class CatalogBrowserPageModule {}
