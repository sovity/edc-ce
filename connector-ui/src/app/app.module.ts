/*
 * Copyright 2022 Eclipse Foundation and Contributors
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
 *     Eclipse Foundation - initial setup of the eclipse-edc/DataDashboard UI
 *     sovity - continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {
  HTTP_INTERCEPTORS,
  HttpClient,
  HttpClientModule,
} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {TitleStrategy} from '@angular/router';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {NgxsModule} from '@ngxs/store';
import {NgChartsModule} from 'ng2-charts';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ApiKeyInterceptor} from './core/services/api/api-key.interceptor';
import {CustomPageTitleStrategy} from './core/services/page-title-strategy';
import {SharedModule} from './shared/shared.module';

@NgModule({
  imports: [
    // Angular
    BrowserAnimationsModule,
    BrowserModule,
    HttpClientModule,

    //Translation
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: (http: HttpClient) => new TranslateHttpLoader(http),
        deps: [HttpClient],
      },
    }),

    // NgXs
    NgxsModule.forRoot([]),

    // Third Party
    NgChartsModule.forRoot(),

    // Features
    SharedModule,

    // Routing
    AppRoutingModule,
  ],
  declarations: [AppComponent],
  providers: [
    HttpClient,
    {provide: HTTP_INTERCEPTORS, multi: true, useClass: ApiKeyInterceptor},
    {provide: TitleStrategy, useClass: CustomPageTitleStrategy},
  ],
  bootstrap: [AppComponent],
  exports: [TranslateModule],
})
export class AppModule {}
