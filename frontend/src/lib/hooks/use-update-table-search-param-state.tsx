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
import {type Table} from '@tanstack/react-table';

export const useUpdateTableSearchParamState = <TData,>(
  table: Table<TData>,
  disabled = false,
) => {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const state = table.getState();

  useEffect(() => {
    const params = new URLSearchParams(searchParams);
    const globalFilter: unknown = state.globalFilter;
    const {sorting, pagination} = state;

    if (!disabled) {
      if (typeof globalFilter === 'string' && globalFilter) {
        params.set('search', globalFilter);
      } else {
        params.delete('search');
      }

      params.delete('sort');
      sorting.forEach((sort) => {
        params.append('sort', JSON.stringify(sort));
      });
      if (pagination.pageIndex !== 0) {
        params.set('page', String(pagination.pageIndex + 1));
      } else {
        params.delete('page');
      }
      if (pagination.pageSize !== 10) {
        params.set('pageSize', String(pagination.pageSize));
      } else {
        params.delete('pageSize');
      }

      if (params.toString() !== searchParams.toString()) {
        router.replace(`${pathname}?${params.toString()}`, {scroll: false});
      }
    }
  }, [state, disabled, pathname, router, searchParams]);
};
