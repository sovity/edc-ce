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
import {
  API_KEY,
  CONNECTOR_DATAMANAGEMENT_API,
  CONNECTOR_ORIGINATOR
} from "../edc-dmgmt-client";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";


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
    MatSnackBarModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  declarations: [
    AppComponent,
    NavigationComponent,
  ],
  providers: [
    MatDatepickerModule,
    {
      provide: APP_INITIALIZER,
      useFactory: (configService: AppConfigService) => () => configService.loadConfig(),
      deps: [AppConfigService],
      multi: true
    },
    {
      provide: CONNECTOR_ORIGINATOR,
      useFactory: (s: AppConfigService) => s.getConfig()?.originator,
      deps: [AppConfigService]
    },
    {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}},
    {
      provide: CONNECTOR_DATAMANAGEMENT_API,
      useFactory: (s: AppConfigService) => s.getConfig()?.dataManagementApiUrl,
      deps: [AppConfigService]
    },
    {
      provide: 'HOME_CONNECTOR_STORAGE_ACCOUNT',
      useFactory: (s: AppConfigService) => s.getConfig()?.storageAccount,
      deps: [AppConfigService]
    },
    {provide: API_KEY, useFactory: (s: AppConfigService) => s.getConfig()?.apiKey, deps: [AppConfigService]},
    {
      provide: 'STORAGE_TYPES',
      useFactory: () => [{assetIdEdcc: "AzureStorage", name: "AzureStorage"}, {assetIdEdcc: "AmazonS3", name: "AmazonS3"}],
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
