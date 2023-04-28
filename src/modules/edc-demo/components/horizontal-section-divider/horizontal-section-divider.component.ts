import {Component, HostBinding, Input} from '@angular/core';

@Component({
  selector: 'edc-demo-horizontal-section-divider',
  templateUrl: './horizontal-section-divider.component.html',
})
export class HorizontalSectionDividerComponent {
  @HostBinding('class.flex')
  @HostBinding('class.items-center')
  cls = true;

  @Input()
  text: string = '';
}
