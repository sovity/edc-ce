/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useConfig} from '@/lib/hooks/use-config';
import SidebarLayout from './components/layout/sidebar-layout';
import React from 'react';

export default function AuthenticatedLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  useConfig();

  return <SidebarLayout>{children}</SidebarLayout>;
}
