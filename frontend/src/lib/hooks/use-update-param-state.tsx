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

export const useUpdateParamState = (search: string, page: number) => {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  useEffect(() => {
    const params = new URLSearchParams(searchParams);

    if (search) {
      params.set('search', search);
    } else {
      params.delete('search');
    }

    if (page > 0) {
      params.set('page', page.toString());
    } else {
      params.delete('page');
    }

    if (params.toString() !== searchParams.toString()) {
      router.replace(`${pathname}?${params.toString()}`, {scroll: false});
    }
  }, [search, pathname, router, searchParams, page]);
};
