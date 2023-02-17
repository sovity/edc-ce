import {Component, HostBinding} from '@angular/core';

@Component({
  selector: 'edc-demo-loading-state',
  templateUrl: './loading-state.component.html',
})
export class LoadingStateComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-center')
  @HostBinding('class.items-center')
  cls = true;
}
