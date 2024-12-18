import {CommonModule} from '@angular/common';
import {HttpClient} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {SharedModule} from '../../shared/shared.module';
import {AssetEditPageModule} from './asset-edit-page/asset-edit-page.module';
import {AssetListPageModule} from './asset-list-page/asset-list-page.module';
import {CatalogBrowserPageModule} from './catalog-browser-page/catalog-browser-page.module';
import {ConnectorUiRoutingModule} from './connector-ui-routing.module';
import {ConnectorUiComponent} from './connector-ui.component';
import {ContractAgreementPageModule} from './contract-agreement-page/contract-agreement-page.module';
import {ContractDefinitionPageModule} from './contract-definition-page/contract-definition-page.module';
import {DashboardPageModule} from './dashboard-page/dashboard-page.module';
import {LocationHistoryUtils} from './logout-page/location-history-utils';
import {LogoutPageModule} from './logout-page/logout-page.module';
import {PreviousRouteListener} from './logout-page/previous-route-listener';
import {PageNotFoundPageModule} from './page-not-found-page/page-not-found-page.module';
import {PolicyDefinitionCreatePageModule} from './policy-definition-create-page/policy-definition-create-page.module';
import {PolicyDefinitionPageModule} from './policy-definition-page/policy-definition-page.module';
import {TransferHistoryPageModule} from './transfer-history-page/transfer-history-page.module';

@NgModule({
  imports: [
    // Angular
    CommonModule,

    // Features
    SharedModule,

    // Pages
    AssetListPageModule,
    AssetEditPageModule,
    CatalogBrowserPageModule,
    ContractAgreementPageModule,
    ContractDefinitionPageModule,
    DashboardPageModule,
    LogoutPageModule,
    PolicyDefinitionPageModule,
    PolicyDefinitionCreatePageModule,
    TransferHistoryPageModule,
    PageNotFoundPageModule,

    // Routing
    ConnectorUiRoutingModule,
  ],
  declarations: [ConnectorUiComponent],
  providers: [PreviousRouteListener, LocationHistoryUtils, HttpClient],
})
export class ConnectorUiModule {
  constructor(previousRouteListener: PreviousRouteListener) {
    // Ensure PreviousRouteListener is instantiated
  }
}
