import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgxsModule} from '@ngxs/store';
import {NgChartsModule} from 'ng2-charts';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {PageNotFoundComponent} from './component-library/error-404-component/page-not-found.component';
import {provideAppConfig} from './core/config/app-config-initializer';
import {ApiKeyInterceptor} from './core/services/api/api-key.interceptor';
import {SharedModule} from './shared.module';

@NgModule({
  imports: [
    // Angular
    BrowserAnimationsModule,
    BrowserModule,
    HttpClientModule,

    SharedModule,

    // NgXs
    NgxsModule.forRoot([]),

    // Third Party
    NgChartsModule.forRoot(),

    // Routing
    AppRoutingModule,
  ],
  declarations: [AppComponent, PageNotFoundComponent],
  providers: [
    provideAppConfig(),

    {provide: HTTP_INTERCEPTORS, multi: true, useClass: ApiKeyInterceptor},
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
