import {Component, HostBinding, Input} from '@angular/core';
import {PropertyGridGroup} from './property-grid-group';

@Component({
  selector: 'property-grid-group',
  templateUrl: './property-grid-group.component.html',
})
export class PropertyGridGroupComponent {
  @Input()
  propGroups: PropertyGridGroup[] = [];

  @Input()
  columns: number = 3;

  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-start')
  cls = true;
}
