import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AssetEditPageComponent} from './asset-edit-page/asset-edit-page/asset-edit-page.component';
import {AssetPageComponent} from './asset-page/asset-page/asset-page.component';
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
    data: {title: 'Dashboard'},
  },
  {
    path: 'create-asset',
    component: AssetEditPageComponent,
    data: {title: 'Create Data Offer'},
  },
  {
    path: 'edit-asset/:id',
    component: AssetEditPageComponent,
    data: {title: 'Edit Asset'},
  },
  {
    path: 'catalog-browser',
    component: CatalogBrowserPageComponent,
    data: {title: 'Catalog Browser'},
  },
  {
    path: 'contracts',
    component: ContractAgreementPageComponent,
    data: {title: 'Contracts'},
  },
  {
    path: 'transfer-history',
    component: TransferHistoryPageComponent,
    data: {title: 'Transfer History'},
  },
  {
    path: 'my-assets', // must not be "assets" to prevent conflict with assets directory
    component: AssetPageComponent,
    data: {title: 'Assets'},
  },
  {
    path: 'policies',
    component: PolicyDefinitionPageComponent,
    data: {title: 'Policies'},
  },
  {
    path: 'policies/create',
    component: PolicyDefinitionCreatePageComponent,
    data: {title: 'Create Policy'},
  },
  {
    path: 'contract-definitions',
    component: ContractDefinitionPageComponent,
    data: {title: 'Contract Definitions'},
  },
  {
    path: 'logout',
    component: LogoutPageComponent,
    data: {title: 'Logout'},
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
