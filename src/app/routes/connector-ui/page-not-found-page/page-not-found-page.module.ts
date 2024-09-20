import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {PageNotFoundPageComponent} from './page-not-found-page.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    RouterModule,
  ],
  declarations: [PageNotFoundPageComponent],
  exports: [PageNotFoundPageComponent],
})
export class PageNotFoundPageModule {}
