/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useMemo, useState} from 'react';
import {searchFilter} from '@/lib/utils/build-word-filter';

export interface WordFilter<T> {
  filter: string;
  setFilter: (filter: string) => void;
  items: T[];
}

export const useWordFilter = <T,>(
  data: T[],
  fieldsFn: (it: T) => string[],
  initiaFilter = '',
): WordFilter<T> => {
  const [filter, setFilter] = useState(initiaFilter);

  const items = useMemo(
    () =>
      data.filter((item) => {
        const allValues = fieldsFn(item).join(' ');
        return searchFilter(filter, allValues);
      }),
    [data, fieldsFn, filter],
  );

  return {filter, setFilter, items};
};
