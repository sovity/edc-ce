/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type UseQueryResult} from '@tanstack/react-query';

export interface MatchQueryStateArgs<T> {
  query: UseQueryResult<T, unknown>;
  loading: () => JSX.Element;
  success: (result: T) => JSX.Element;
}

export const matchQueryState = <T,>({
  query,
  loading,
  success,
}: MatchQueryStateArgs<T>): JSX.Element => {
  if (query.isLoading) {
    return loading();
  }

  // we assume that error is propagated to nearest error boundary
  if (query.isSuccess) {
    return success(query.data);
  }
  throw new Error(
    'Query is not loading or successful, this should not happen.' +
      query.status,
  );
};
