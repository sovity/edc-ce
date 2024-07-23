import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MAT_DATE_LOCALE, MatNativeDateModule} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {
  MAT_FORM_FIELD_DEFAULT_OPTIONS,
  MatFormFieldDefaultOptions,
} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatToolbarModule} from '@angular/material/toolbar';
import {
  MAT_TOOLTIP_DEFAULT_OPTIONS,
  MAT_TOOLTIP_DEFAULT_OPTIONS_FACTORY,
  MatTooltipDefaultOptions,
} from '@angular/material/tooltip';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgxsModule} from '@ngxs/store';
import {NgChartsModule} from 'ng2-charts';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {PageNotFoundComponent} from './component-library/error-404-component/page-not-found.component';
import {provideAppConfig} from './core/config/app-config-initializer';
import {ApiKeyInterceptor} from './core/services/api/api-key.interceptor';

@NgModule({
  imports: [
    // Angular
    BrowserAnimationsModule,
    BrowserModule,
    HttpClientModule,

    // Angular Material
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatDialogModule,
    MatIconModule,
    MatListModule,
    MatNativeDateModule,
    MatSidenavModule,
    MatSnackBarModule,
    MatToolbarModule,

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

    {provide: MAT_DATE_LOCALE, useValue: 'en-GB'},
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: {
        appearance: 'outline',
        color: 'accent',
      } as MatFormFieldDefaultOptions,
    },
    {
      provide: MAT_TOOLTIP_DEFAULT_OPTIONS,
      useValue: <MatTooltipDefaultOptions>{
        ...MAT_TOOLTIP_DEFAULT_OPTIONS_FACTORY(),
        disableTooltipInteractivity: true,
      },
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
