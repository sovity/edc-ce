/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useBreadcrumbItems} from '@/lib/breadcrumb-items';
import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useTitle} from '@/lib/hooks/use-title';
import {useTranslations} from 'next-intl';
import AssetTable from './components/asset-table';
import PageContainer from '@/components/page-container';

export default function AssetListPage() {
  const t = useTranslations();
  useTitle(t('General.assets'));
  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([breadcrumbItems.assets.listPage()]);

  return (
    <PageContainer>
      <AssetTable />
    </PageContainer>
  );
}
