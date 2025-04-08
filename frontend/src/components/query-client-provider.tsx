/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useState} from 'react';
import {
  QueryClient,
  QueryClientProvider as Provider,
} from '@tanstack/react-query';

export const QueryClientProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [queryClient] = useState(() => new QueryClient());
  return <Provider client={queryClient}>{children}</Provider>;
};
