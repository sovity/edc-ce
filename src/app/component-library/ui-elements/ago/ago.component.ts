import {Component, Input} from '@angular/core';

/**
 * Displays a date as estimated relative time (e.g. "3 days ago").
 *
 * But also shows the full date as a tooltip.
 */
@Component({
  selector: 'ago',
  template: `<span [title]="date | date : 'EEEE yyyy-MM-dd hh:mm'">{{
    date | ago | async
  }}</span>`,
})
export class AgoComponent {
  @Input()
  date?: Date | null;
}
