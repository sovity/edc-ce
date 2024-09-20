import {Component, HostBinding, Input} from '@angular/core';

@Component({
  selector: 'empty-state',
  templateUrl: './empty-state.component.html',
})
export class EmptyStateComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-center')
  @HostBinding('class.items-center')
  @HostBinding('class.uppercase')
  @HostBinding('class.text-slate')
  cls = true;

  @Input()
  emptyMessage = '';
}
