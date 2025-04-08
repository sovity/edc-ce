/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useEffect} from 'react';
import {
  type BreadcrumbItem,
  useBreadcrumbStore,
} from '../stores/breadcrumb-store';

export const useBreadcrumbs = (newItems: BreadcrumbItem[]) => {
  const {setItems} = useBreadcrumbStore();

  useEffect(() => {
    setItems(newItems);
  }, [newItems, setItems]);
};
