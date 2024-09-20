import {Pipe, PipeTransform} from '@angular/core';

/**
 * `Object.values(...)` can't be used from angular templates.
 */
@Pipe({name: 'values'})
export class ValuesPipe implements PipeTransform {
  transform<T>(obj: T): T[keyof T][] {
    return Object.values(obj || {});
  }
}
