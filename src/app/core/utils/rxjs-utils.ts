import {OperatorFunction} from 'rxjs';
import {filter} from 'rxjs/operators';

/**
 * Simple not null filtering RXJS Operator.
 *
 * The trick is that it removes the "null | undefined" from the resulting stream type signature.
 */
export function filterNotNull<T>(): OperatorFunction<T | null | undefined, T> {
  return filter((it) => it != null) as any;
}
