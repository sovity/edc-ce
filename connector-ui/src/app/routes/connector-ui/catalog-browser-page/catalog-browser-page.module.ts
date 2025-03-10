/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
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
