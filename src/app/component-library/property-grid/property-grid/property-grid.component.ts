import {Component, HostBinding, Input, TrackByFunction} from '@angular/core';
import {PropertyGridField} from './property-grid-field';

@Component({
  selector: 'property-grid',
  templateUrl: './property-grid.component.html',
})
export class PropertyGridComponent {
  @Input()
  props: PropertyGridField[] = [];

  @Input()
  columns: number = 3;
  @HostBinding('class.flex')
  @HostBinding('class.flex-row')
  @HostBinding('class.flex-wrap')
  @HostBinding('class.gap-[10px]')
  @HostBinding('class.justify-start')
  cls = true;

  trackByIndex: TrackByFunction<any> = (index: number) => index;
}
