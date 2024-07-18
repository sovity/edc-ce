import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {EdcUiFeature} from '../../core/config/profiles/edc-ui-feature';
import {AssetPageComponent} from './asset-page/asset-page/asset-page.component';
import {CatalogBrowserPageComponent} from './catalog-browser-page/catalog-browser-page/catalog-browser-page.component';
import {ConnectorUiComponent} from './connector-ui.component';
import {ContractAgreementPageComponent} from './contract-agreement-page/contract-agreement-page/contract-agreement-page.component';
import {ContractDefinitionPageComponent} from './contract-definition-page/contract-definition-page/contract-definition-page.component';
import {DashboardPageComponent} from './dashboard-page/dashboard-page/dashboard-page.component';
import {LogoutPageComponent} from './logout-page/logout-page.component';
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
    data: {title: 'Dashboard', icon: 'data_usage'},
  },
  {
    path: 'catalog-browser',
    component: CatalogBrowserPageComponent,
    data: {title: 'Catalog Browser', icon: 'sim_card'},
  },
  {
    path: 'contracts',
    component: ContractAgreementPageComponent,
    data: {title: 'Contracts', icon: 'assignment_turned_in'},
  },
  {
    path: 'transfer-history',
    component: TransferHistoryPageComponent,
    data: {title: 'Transfer History', icon: 'assignment'},
  },
  {
    path: 'my-assets', // must not be "assets" to prevent conflict with assets directory
    component: AssetPageComponent,
    data: {title: 'Assets', icon: 'upload'},
  },
  {
    path: 'policies',
    component: PolicyDefinitionPageComponent,
    data: {title: 'Policies', icon: 'policy'},
  },
  {
    path: 'contract-definitions',
    component: ContractDefinitionPageComponent,
    data: {title: 'Contract Definitions', icon: 'rule'},
  },
  {
    path: 'logout',
    component: LogoutPageComponent,
    data: {
      title: 'Logout',
      icon: 'logout',
      requiresFeature: 'logout-button' as EdcUiFeature,
    },
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
