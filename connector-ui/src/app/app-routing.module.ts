/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
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
