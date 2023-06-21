import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {LegalNoticePageComponent} from './legal-notice-page/legal-notice-page.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    RouterModule,
  ],
  declarations: [LegalNoticePageComponent],
  exports: [LegalNoticePageComponent],
})
export class LegalNoticePageModule {}
