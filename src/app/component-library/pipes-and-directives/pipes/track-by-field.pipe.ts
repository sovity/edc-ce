import {Pipe, PipeTransform, TrackByFunction} from '@angular/core';

/**
 * Creates Track By Function for ngFor loops
 */
@Pipe({name: 'trackByField'})
export class TrackByFieldPipe implements PipeTransform {
  transform(key: string): TrackByFunction<any> {
    return (_, item) => (item == null ? null : item[key]);
  }
}
