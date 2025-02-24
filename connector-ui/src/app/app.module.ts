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
import {provideAppConfig} from './core/config/app-config-initializer';
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
    provideAppConfig(),

    {provide: HTTP_INTERCEPTORS, multi: true, useClass: ApiKeyInterceptor},
    {provide: TitleStrategy, useClass: CustomPageTitleStrategy},
  ],
  bootstrap: [AppComponent],
  exports: [TranslateModule],
})
export class AppModule {}
