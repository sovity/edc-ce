import {Observable, OperatorFunction, defer, from} from 'rxjs';
import {filter, tap} from 'rxjs/operators';

/**
 * Simple not null filtering RXJS Operator.
 *
 * The trick is that it removes the "null | undefined" from the resulting stream type signature.
 */
export function filterNotNull<T>(): OperatorFunction<T | null | undefined, T> {
  return filter((it) => it != null) as any;
}

export function throwIfNull<T>(
  msg: string,
): OperatorFunction<T | null | undefined, T> {
  return tap((it) => {
    if (it == null) {
      throw new Error(msg);
    }
  }) as OperatorFunction<T | null | undefined, T>;
}

export const toObservable = <T>(fn: () => Promise<T>): Observable<T> =>
  defer(() => from(fn()));
