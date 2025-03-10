/*
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 * Copyright 2025 sovity GmbH
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
 *     Fraunhofer FIT - contributed initial internationalization support
 *     sovity - continued development
 */
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
