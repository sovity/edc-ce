import {LayoutModule} from '@angular/cdk/layout';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatNativeDateModule} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {
  MAT_FORM_FIELD_DEFAULT_OPTIONS,
  MatFormFieldDefaultOptions,
} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatToolbarModule} from '@angular/material/toolbar';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgChartsModule} from 'ng2-charts';
import {PreviousRouteListener} from '../edc-demo/components/logout/previous-route-listener';
import {EdcDemoModule} from '../edc-demo/edc-demo.module';
import {API_KEY, CONNECTOR_DATAMANAGEMENT_API} from '../edc-dmgmt-client';
import {ApiKeyInterceptor} from './api-key.interceptor';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NavigationComponent} from './components/navigation/navigation.component';
import {loadAppConfigOnStartup} from './config/app-config-initializer';
import {provideConfigProperty} from './config/app-config-injection-utils';
import {AppConfigBuilder} from './config/app-config.builder';
import {AppConfigFetcher} from './config/app-config.fetcher';
import {AppConfigMerger} from './config/app-config.merger';
import {AppConfigService} from './config/app-config.service';

@NgModule({
  imports: [
    // Angular
    BrowserAnimationsModule,
    BrowserModule,

    // Angular Material
    MatButtonModule,
    MatDatepickerModule,
    MatIconModule,
    MatListModule,
    MatNativeDateModule,
    MatSidenavModule,
    MatSnackBarModule,
    MatToolbarModule,

    // Third Party
    NgChartsModule.forRoot(),

    // Feature Modules
    EdcDemoModule,
    LayoutModule,

    // Routing
    AppRoutingModule,
  ],
  declarations: [AppComponent, NavigationComponent],
  providers: [
    AppConfigMerger,
    AppConfigFetcher,
    AppConfigBuilder,
    AppConfigService,

    // Load and build config on page load
    loadAppConfigOnStartup(),

    // Provide individual properties of config for better Angular Component APIs
    provideConfigProperty(CONNECTOR_DATAMANAGEMENT_API, 'dataManagementApiUrl'),
    provideConfigProperty(API_KEY, 'dataManagementApiKey'),

    {provide: HTTP_INTERCEPTORS, multi: true, useClass: ApiKeyInterceptor},

    PreviousRouteListener,
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: {
        appearance: 'outline',
        color: 'accent',
      } as MatFormFieldDefaultOptions,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
  constructor(
    // Ensure PreviousRouteListener is instantiated
    previousRouteListener: PreviousRouteListener,
  ) {}
}
