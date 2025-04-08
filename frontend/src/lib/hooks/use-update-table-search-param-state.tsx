/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useEffect} from 'react';
import {usePathname, useRouter, useSearchParams} from 'next/navigation';
import {type SortingState} from '@tanstack/react-table';

export const useUpdateTableSearchParamState = (
  globalFilter: string,
  sorting: SortingState,
) => {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  useEffect(() => {
    const params = new URLSearchParams(searchParams);

    if (globalFilter) {
      params.set('search', globalFilter);
    } else {
      params.delete('search');
    }

    params.delete('sort');
    sorting.forEach((sort) => {
      params.append('sort', JSON.stringify(sort));
    });

    if (params.toString() !== searchParams.toString()) {
      router.replace(`${pathname}?${params.toString()}`, {scroll: false});
    }
  }, [globalFilter, pathname, router, searchParams, sorting]);
};
