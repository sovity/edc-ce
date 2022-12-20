import {APP_INITIALIZER, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

import {LayoutModule} from '@angular/cdk/layout';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';
import {NavigationComponent} from './components/navigation/navigation.component';
import {EdcDemoModule} from '../edc-demo/edc-demo.module';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from '@angular/material/form-field';
import {AppConfigService} from "./app-config.service";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import { CONNECTOR_MANAGEMENT_API} from "./variables";
import {Configuration} from "../mgmt-api-client";
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {EdcApiKeyInterceptor} from "./edc.apikey.interceptor";
import {environment} from "../../environments/environment";


@NgModule({
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    LayoutModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    EdcDemoModule,
    MatSnackBarModule
  ],
  declarations: [
    AppComponent,
    NavigationComponent,
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: (configService: AppConfigService) => () => configService.loadConfig(),
      deps: [AppConfigService],
      multi: true
    },
    {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}},
    {
      provide: CONNECTOR_MANAGEMENT_API,
      useFactory: (s: AppConfigService) => s.getConfig()?.dataManagementApiUrl,
      deps: [AppConfigService]
    },
    {
      provide: 'HOME_CONNECTOR_STORAGE_ACCOUNT',
      useFactory: (s: AppConfigService) => s.getConfig()?.storageAccount,
      deps: [AppConfigService]
    },
    {
      provide: 'STORAGE_TYPES',
      useFactory: () => [{id: "AzureStorage", name: "AzureStorage"}, {id: "AmazonS3", name: "AmazonS3"}],
    },
    {
      provide: Configuration,
      useFactory: (s: AppConfigService) => {
        return new Configuration({
          basePath: s.getConfig()?.dataManagementApiUrl
        });
      },
      deps: [AppConfigService]
    },
    {
      provide: HTTP_INTERCEPTORS, multi: true, useFactory: () => {
        let i = new EdcApiKeyInterceptor();
        // TODO: read this from app.config.json??
        i.apiKey = environment.apiKey
        return i;
      }, deps: [AppConfigService]
    }

  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
