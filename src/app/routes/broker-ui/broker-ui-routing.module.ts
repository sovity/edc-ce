import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {BrokerUiComponent} from './broker-ui.component';
import {CatalogPageComponent} from './catalog-page/catalog-page/catalog-page.component';
import {ConnectorPageComponent} from './connector-page/connector-page/connector-page.component';
import {LegalNoticePageComponent} from './legal-notice-page/legal-notice-page/legal-notice-page.component';

export const routes: Routes = [
  {
    path: '',
    component: CatalogPageComponent,
    data: {
      title: 'Data Offers',
      icon: 'sim_card',
      exactPathMatch: true,
    },
    pathMatch: 'full',
  },
  {
    path: 'connectors',
    component: ConnectorPageComponent,
    data: {
      title: 'Connectors',
      icon: 'link',
      hideInNav: true,
    },
  },
  {
    path: 'legal-notice',
    component: LegalNoticePageComponent,
    data: {
      title: 'Legal Notice',
      icon: 'info',
      hideInNav: true,
    },
  },
];

@NgModule({
  imports: [
    RouterModule.forChild([
      {path: '', component: BrokerUiComponent, children: routes},
    ]),
  ],
  exports: [RouterModule],
})
export class BrokerUiRoutingModule {}
