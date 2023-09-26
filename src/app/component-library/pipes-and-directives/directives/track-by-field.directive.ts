import {NgForOf} from '@angular/common';
import {Attribute, Directive, TrackByFunction, inject} from '@angular/core';

export const newTrackByFn =
  <T>(key: keyof T): TrackByFunction<T> =>
  (_, item: T) =>
    item == null ? null : item[key] ?? item;

/**
 * Creates Track By Function for ngFor loops
 */
@Directive({
  selector: '[ngFor][ngForOf][trackByField]',
})
export class TrackByFieldDirective {
  constructor(
    @Attribute('trackByField') private readonly trackByField: string,
  ) {
    const ngForOf = inject(NgForOf<unknown>, {self: true});
    ngForOf.ngForTrackBy = newTrackByFn(this.trackByField);
  }
}
