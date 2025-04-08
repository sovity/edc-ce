/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {api} from '@/lib/api/client';
import {useQueryWrapper} from '@/lib/hooks/use-query-wrapper';
import {queryKeys} from '@/lib/queryKeys';

export const useConfig = () => {
  const query = useQueryWrapper(queryKeys.config.key(), () =>
    api.uiApi.uiConfig(),
  );

  return query?.data;
};
