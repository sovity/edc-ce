/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useState} from 'react';
import {useRouter} from 'next/navigation';

export const useTab = (initialTab: string, url: (t: string) => string) => {
  const [tab, setTab] = useState(initialTab);
  const router = useRouter();

  const onTabChange = (tab: string) => {
    setTab(tab);

    router.push(url(tab));
  };

  return {tab, onTabChange};
};
