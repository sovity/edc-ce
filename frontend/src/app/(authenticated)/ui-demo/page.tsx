/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useTitle} from '@/lib/hooks/use-title';
import * as React from 'react';
import PageContainer from '@/components/page-container';
import UiDemo from '@/app/(authenticated)/ui-demo/ui-demo';

export default function Page() {
  useTitle('UI / UX Testing Page');
  useBreadcrumbs([
    {
      label: 'UI / UX Testing Page',
      href: '/ui-demo',
    },
  ]);

  return (
    <PageContainer className={'flex flex-col gap-8'}>
      <UiDemo />
    </PageContainer>
  );
}
