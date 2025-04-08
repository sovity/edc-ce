/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useSearchParams} from 'next/navigation';

export const useUrlParams = () => {
  const searchParams = useSearchParams();
  return {
    search: searchParams.get('search') ?? undefined,
    sort: searchParams.getAll('sort'),
    callbackUrl: searchParams.get('callbackUrl') ?? undefined,
    page: searchParams.get('page') ? Number(searchParams.get('page')) : 0,
  };
};
