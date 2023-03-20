import {Component, HostBinding, Input} from '@angular/core';
import {FetchError} from '../../models/fetched';

@Component({
  selector: 'edc-demo-error-state',
  templateUrl: './error-state.component.html',
})
export class ErrorStateComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-center')
  @HostBinding('class.items-center')
  cls = true;

  @Input()
  error: FetchError | undefined;
}
