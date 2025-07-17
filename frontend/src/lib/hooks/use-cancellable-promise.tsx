/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

'use client';

import {useRef, useCallback} from 'react';

export type CancellablePromise = <T>(promise: Promise<T>) => Promise<T>;

export function useCancellablePromise() {
  const currentRequestId = useRef(0);

  const cancellablePromise = useCallback(
    <T,>(promise: Promise<T>): Promise<T> => {
      const requestId = ++currentRequestId.current;

      return new Promise((resolve, reject) => {
        promise
          .then((result) => {
            if (requestId === currentRequestId.current) {
              resolve(result);
            }
          })
          .catch((error: Error) => {
            if (requestId === currentRequestId.current) {
              reject(error);
            }
          });
      });
    },
    [],
  );

  return cancellablePromise;
}
