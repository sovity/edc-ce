import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatChipsModule} from '@angular/material/chips';
import {
  DateAdapter,
  MAT_DATE_LOCALE,
  MatNativeDateModule,
} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatDividerModule} from '@angular/material/divider';
import {
  MAT_FORM_FIELD_DEFAULT_OPTIONS,
  MatFormFieldDefaultOptions,
  MatFormFieldModule,
} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {MatMenuModule} from '@angular/material/menu';
import {MatSelectModule} from '@angular/material/select';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatToolbarModule} from '@angular/material/toolbar';
import {
  MAT_TOOLTIP_DEFAULT_OPTIONS,
  MAT_TOOLTIP_DEFAULT_OPTIONS_FACTORY,
  MatTooltipDefaultOptions,
  MatTooltipModule,
} from '@angular/material/tooltip';
import {CustomDateAdapter} from './core/adapters/custom-date-adapter';

const MODULES = [
  MatButtonModule,
  MatCardModule,
  MatChipsModule,
  MatDatepickerModule,
  MatDialogModule,
  MatDividerModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatMenuModule,
  MatNativeDateModule,
  MatSelectModule,
  MatSidenavModule,
  MatSnackBarModule,
  MatToolbarModule,
  MatTooltipModule,
];

const COMPONENTS: NgModule['declarations'] = [];

@NgModule({
  imports: [
    // Angular
    CommonModule,

    // Angular Material
    ...MODULES,
  ],
  exports: [...MODULES, ...COMPONENTS],
  declarations: COMPONENTS,
  providers: [
    {provide: DateAdapter, useClass: CustomDateAdapter},

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
})
export class SharedModule {}
