import {NgModule} from '@angular/core';
import {ROUTES, RouterModule, Routes} from '@angular/router';
import {APP_CONFIG, AppConfig} from './core/config/app-config';
import {PageNotFoundPageComponent} from './routes/connector-ui/page-not-found-page/page-not-found-page.component';

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
          case 'connector-ui':
            routes.push({
              path: '',
              loadChildren: () =>
                import('./routes/connector-ui/connector-ui.module').then(
                  (m) => m.ConnectorUiModule,
                ),
            });
            break;
          default:
            throw new Error(`Unhandled PageSet: ${config.routes}`);
        }
        routes.push({path: '**', component: PageNotFoundPageComponent});
        return routes;
      },
    },
  ],
})
export class AppRoutingModule {}
