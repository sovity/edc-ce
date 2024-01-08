import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatNativeDateModule} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatToolbarModule} from '@angular/material/toolbar';
import {PipesAndDirectivesModule} from '../../component-library/pipes-and-directives/pipes-and-directives.module';
import {UiElementsModule} from '../../component-library/ui-elements/ui-elements.module';
import {AssetPageModule} from './asset-page/asset-page.module';
import {CatalogBrowserPageModule} from './catalog-browser-page/catalog-browser-page.module';
import {ConnectorUiRoutingModule} from './connector-ui-routing.module';
import {ConnectorUiComponent} from './connector-ui.component';
import {ContractAgreementPageModule} from './contract-agreement-page/contract-agreement-page.module';
import {ContractDefinitionPageModule} from './contract-definition-page/contract-definition-page.module';
import {DashboardPageModule} from './dashboard-page/dashboard-page.module';
import {LocationHistoryUtils} from './logout-page/location-history-utils';
import {LogoutPageModule} from './logout-page/logout-page.module';
import {PreviousRouteListener} from './logout-page/previous-route-listener';
import {PolicyDefinitionPageModule} from './policy-definition-page/policy-definition-page.module';
import {TransferHistoryPageModule} from './transfer-history-page/transfer-history-page.module';

@NgModule({
  imports: [
    // Angular
    CommonModule,

    // Angular Material
    MatButtonModule,
    MatDatepickerModule,
    MatIconModule,
    MatListModule,
    MatNativeDateModule,
    MatSidenavModule,
    MatSnackBarModule,
    MatToolbarModule,

    // Features
    PipesAndDirectivesModule,
    UiElementsModule,

    // Pages
    AssetPageModule,
    CatalogBrowserPageModule,
    ContractAgreementPageModule,
    ContractDefinitionPageModule,
    DashboardPageModule,
    LogoutPageModule,
    PolicyDefinitionPageModule,
    TransferHistoryPageModule,

    // Routing
    ConnectorUiRoutingModule,
  ],
  declarations: [ConnectorUiComponent],
  providers: [PreviousRouteListener, LocationHistoryUtils],
})
export class ConnectorUiModule {
  constructor(previousRouteListener: PreviousRouteListener) {
    // Ensure PreviousRouteListener is instantiated
  }
}
