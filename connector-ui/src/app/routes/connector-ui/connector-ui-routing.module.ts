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
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AssetEditPageComponent} from './asset-edit-page/asset-edit-page/asset-edit-page.component';
import {AssetListPageComponent} from './asset-list-page/asset-list-page/asset-list-page.component';
import {CatalogBrowserPageComponent} from './catalog-browser-page/catalog-browser-page/catalog-browser-page.component';
import {ConnectorUiComponent} from './connector-ui.component';
import {ContractAgreementPageComponent} from './contract-agreement-page/contract-agreement-page/contract-agreement-page.component';
import {ContractDefinitionPageComponent} from './contract-definition-page/contract-definition-page/contract-definition-page.component';
import {DashboardPageComponent} from './dashboard-page/dashboard-page/dashboard-page.component';
import {LogoutPageComponent} from './logout-page/logout-page.component';
import {PolicyDefinitionCreatePageComponent} from './policy-definition-create-page/policy-definition-create-page/policy-definition-create-page.component';
import {PolicyDefinitionPageComponent} from './policy-definition-page/policy-definition-page/policy-definition-page.component';
import {TransferHistoryPageComponent} from './transfer-history-page/transfer-history-page/transfer-history-page.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'dashboard',
    component: DashboardPageComponent,
    data: {title: 'dashboard_page.title'},
  },
  {
    path: 'create-asset',
    component: AssetEditPageComponent,
    data: {title: 'create_data_offer_page.title'},
  },
  {
    path: 'catalog-browser',
    component: CatalogBrowserPageComponent,
    data: {title: 'catalog_browser_page.title'},
  },
  {
    path: 'contracts',
    component: ContractAgreementPageComponent,
    data: {title: 'contract_agreement_page.title'},
  },
  {
    path: 'transfer-history',
    component: TransferHistoryPageComponent,
    data: {title: 'transfer_history_page.title'},
  },
  {
    path: 'my-assets', // must not be "assets" to prevent conflict with assets directory
    component: AssetListPageComponent,
    data: {title: 'asset_list_page.title'},
  },
  {
    path: 'my-assets/:id/edit',
    component: AssetEditPageComponent,
    data: {title: 'edit_asset_page.title'},
  },
  {
    path: 'policies',
    component: PolicyDefinitionPageComponent,
    data: {title: 'policy_definition_page.title'},
  },
  {
    path: 'policies/create',
    component: PolicyDefinitionCreatePageComponent,
    data: {title: 'create_policy_page.title'},
  },
  {
    path: 'data-offers',
    component: ContractDefinitionPageComponent,
    data: {title: 'contract_definition_page.title'},
  },
  {
    path: 'logout',
    component: LogoutPageComponent,
    data: {title: 'logout_page.title'},
  },
];

@NgModule({
  imports: [
    RouterModule.forChild([
      {path: '', component: ConnectorUiComponent, children: routes},
    ]),
  ],
  exports: [RouterModule],
})
export class ConnectorUiRoutingModule {}
