import {Component, HostBinding} from '@angular/core';

@Component({
  selector: 'app-page-not-found',
  templateUrl: './page-not-found.component.html',
})
export class PageNotFoundComponent {
  @HostBinding('class.container')
  @HostBinding('class.flex')
  @HostBinding('class.items-center')
  @HostBinding('class.min-h-screen')
  @HostBinding('class.px-6')
  @HostBinding('class.py-12')
  @HostBinding('class.box-border')
  cls = true;
}
