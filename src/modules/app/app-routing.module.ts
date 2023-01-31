import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AssetViewerComponent} from '../edc-demo/components/asset-viewer/asset-viewer.component';
import {CatalogBrowserComponent} from '../edc-demo/components/catalog-browser/catalog-browser.component';
import {ContractDefinitionViewerComponent} from '../edc-demo/components/contract-definition-viewer/contract-definition-viewer.component';
import {ContractViewerComponent} from '../edc-demo/components/contract-viewer/contract-viewer.component';
import {DashboardComponent} from '../edc-demo/components/dashboard/dashboard.component';
import {LogoutComponent} from '../edc-demo/components/logout/logout.component';
import {PolicyViewComponent} from '../edc-demo/components/policy-view/policy-view.component';
import {TransferHistoryViewerComponent} from '../edc-demo/components/transfer-history/transfer-history-viewer.component';
import {EdcUiFeature} from './config/edc-ui-feature';

export const routes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent,
    data: {title: 'Dashboard', icon: 'data_usage'},
  },
  {
    path: 'catalog-browser',
    component: CatalogBrowserComponent,
    data: {title: 'Catalog Browser', icon: 'sim_card'},
  },
  {
    path: 'contracts',
    component: ContractViewerComponent,
    data: {title: 'Contracts', icon: 'attachment'},
  },
  {
    path: 'transfer-history',
    component: TransferHistoryViewerComponent,
    data: {title: 'Transfer History', icon: 'assignment'},
  },
  {
    path: 'contract-definitions',
    component: ContractDefinitionViewerComponent,
    data: {title: 'Contract Definitions', icon: 'rule'},
  },
  {
    path: 'policies',
    component: PolicyViewComponent,
    data: {title: 'Policies', icon: 'policy'},
  },
  {
    path: 'my-assets', // must not be "assets" to prevent conflict with assets directory
    component: AssetViewerComponent,
    data: {title: 'Assets', icon: 'upload'},
  },
  {
    path: 'logout',
    component: LogoutComponent,
    data: {
      title: 'Logout',
      icon: 'logout',
      requiresFeature: 'logout-button' as EdcUiFeature,
    },
  },
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
