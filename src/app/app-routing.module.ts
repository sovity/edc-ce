import {NgModule} from '@angular/core';
import {
  ROUTES,
  RouterModule,
  Routes,
} from '@angular/router';
import {APP_CONFIG, AppConfig} from './core/config/app-config';
import {PageNotFoundComponent} from "./component-library/error-404-component/page-not-found.component";


@NgModule({
  imports: [RouterModule.forRoot([], {paramsInheritanceStrategy: 'always'})],
  exports: [RouterModule],
  providers: [
    {
      provide: ROUTES,
      deps: [APP_CONFIG],
      multi: true,

      useFactory: (config: AppConfig): Routes => {
        const routes: Routes = [];
        switch (config.routes) {
          case 'broker-ui':
            routes.push({
              path: '', loadChildren: () =>
                import('./routes/broker-ui/broker-ui.module').then(
                  (m) => m.BrokerUiModule,
                )
            });
            break;
          case 'connector-ui':
            routes.push({
              path: '', loadChildren: () =>
                import('./routes/connector-ui/connector-ui.module').then(
                  (m) => m.ConnectorUiModule,
                )
            });
            break;
          default:
            throw new Error(`Unhandled PageSet: ${config.routes}`);
        }
        routes.push({path: '**', component: PageNotFoundComponent})
        return routes;
      },
    },
  ],
})
export class AppRoutingModule {}
