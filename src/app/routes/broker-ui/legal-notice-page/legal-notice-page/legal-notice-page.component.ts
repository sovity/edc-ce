import {Component, HostBinding} from '@angular/core';

@Component({
  selector: 'legal-notice-page',
  templateUrl: './legal-notice-page.component.html',
})
export class LegalNoticePageComponent {
  @HostBinding('class.block')
  @HostBinding('class.p-[20px]')
  cls = true;
}
