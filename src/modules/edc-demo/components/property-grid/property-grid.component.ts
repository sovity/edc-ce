import {Component, HostBinding, Input} from '@angular/core';
import {PropertyGridField} from './property-grid-field';

@Component({
  selector: 'edc-demo-property-grid',
  templateUrl: './property-grid.component.html',
})
export class PropertyGrid {
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
}
