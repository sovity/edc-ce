import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {LogoutPageComponent} from './logout-page.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    RouterModule,
  ],
  declarations: [LogoutPageComponent],
  exports: [LogoutPageComponent],
})
export class LogoutPageModule {}
