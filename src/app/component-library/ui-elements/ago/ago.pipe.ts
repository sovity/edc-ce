import {Pipe, PipeTransform} from '@angular/core';
import {Observable, concat, interval, of} from 'rxjs';
import {distinctUntilChanged, map} from 'rxjs/operators';
import {formatDistanceToNow} from 'date-fns';

/**
 * Displays a date as estimated relative time (e.g. "3 days ago").
 */
@Pipe({name: 'ago'})
export class AgoPipe implements PipeTransform {
  interval$ = concat(of({}), interval(3000));

  transform(date?: Date | null): Observable<string> {
    return this.interval$.pipe(
      map(() => this.ago(date)),
      distinctUntilChanged(),
    );
  }

  ago(date?: Date | null): string {
    if (!date) {
      return '';
    }

    return formatDistanceToNow(date, {addSuffix: true});
  }
}
