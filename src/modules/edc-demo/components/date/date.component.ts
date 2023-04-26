import {Component, Input} from '@angular/core';

@Component({
  selector: 'edc-demo-date',
  template: `<span [title]="date | date : 'EEEE yyyy-MM-dd hh:mm'">{{
    date | date : 'yyyy-MM-dd'
  }}</span>`,
})
export class DateComponent {
  @Input()
  date?: Date | null;
}
