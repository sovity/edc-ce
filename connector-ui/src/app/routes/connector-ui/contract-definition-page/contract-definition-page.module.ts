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
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../../../shared/shared.module';
import {AssetSelectComponent} from './asset-select/asset-select.component';
import {ContractDefinitionCardsComponent} from './contract-definition-cards/contract-definition-cards.component';
import {ContractDefinitionEditorDialog} from './contract-definition-editor-dialog/contract-definition-editor-dialog.component';
import {ContractDefinitionPageComponent} from './contract-definition-page/contract-definition-page.component';
import {PolicySelectComponent} from './policy-select/policy-select.component';

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
    AssetSelectComponent,
    ContractDefinitionCardsComponent,
    ContractDefinitionEditorDialog,
    ContractDefinitionPageComponent,
    PolicySelectComponent,
  ],
  exports: [ContractDefinitionPageComponent],
})
export class ContractDefinitionPageModule {}
