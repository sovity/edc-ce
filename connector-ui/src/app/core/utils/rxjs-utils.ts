/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
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
