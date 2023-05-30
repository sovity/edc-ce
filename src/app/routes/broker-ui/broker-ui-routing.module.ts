import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {BrokerUiComponent} from './broker-ui.component';
import {CatalogBrowserPageComponent} from './catalog-browser-page/catalog-page/catalog-browser-page.component';

export const routes: Routes = [
  {
    path: '',
    component: CatalogBrowserPageComponent,
    data: {
      title: 'Data Offers',
      icon: 'sim_card',
    },
    pathMatch: 'full',
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
