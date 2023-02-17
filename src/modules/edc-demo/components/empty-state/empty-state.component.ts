import {Component, HostBinding, Input} from '@angular/core';

@Component({
  selector: 'edc-demo-empty-state',
  templateUrl: './empty-state.component.html',
})
export class EmptyStateComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-center')
  @HostBinding('class.uppercase')
  cls = true;

  @Input()
  emptyMessage = '';
}
