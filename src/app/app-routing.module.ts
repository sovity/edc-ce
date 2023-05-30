import {NgModule} from '@angular/core';
import {
  LoadChildrenCallback,
  ROUTES,
  RouterModule,
  Routes,
} from '@angular/router';
import {APP_CONFIG, AppConfig} from './core/config/app-config';

@NgModule({
  imports: [RouterModule.forRoot([], {paramsInheritanceStrategy: 'always'})],
  exports: [RouterModule],
  providers: [
    {
      provide: ROUTES,
      deps: [APP_CONFIG],
      multi: true,
      useFactory: (config: AppConfig): Routes => {
        const loadChildRoutes = (
          loadChildren: LoadChildrenCallback,
        ): Routes => [{path: '', loadChildren}];

        switch (config.routes) {
          case 'broker-ui':
            return loadChildRoutes(() =>
              import('./routes/broker-ui/broker-ui.module').then(
                (m) => m.BrokerUiModule,
              ),
            );
          case 'connector-ui':
            return loadChildRoutes(() =>
              import('./routes/connector-ui/connector-ui.module').then(
                (m) => m.ConnectorUiModule,
              ),
            );
          default:
            throw new Error(`Unhandled PageSet: ${config.routes}`);
        }
      },
    },
  ],
})
export class AppRoutingModule {}
