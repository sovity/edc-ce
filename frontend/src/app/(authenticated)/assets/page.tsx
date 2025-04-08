/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {CenteredLoadingSpinner} from '@/components/loading-spinner';
import {api} from '@/lib/api/client';
import {useBreadcrumbItems} from '@/lib/breadcrumb-items';
import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useQueryWrapper} from '@/lib/hooks/use-query-wrapper';
import {useTitle} from '@/lib/hooks/use-title';
import {matchQueryState} from '@/lib/utils/match-query-state';
import {queryKeys} from '@/lib/queryKeys';
import {type AssetPage} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';
import AssetTable from './components/asset-table';
import PageContainer from '@/components/page-container';

export default function AssetListPage() {
  const pageQuery = useQueryWrapper(queryKeys.assets.listPage(), () =>
    api.uiApi.getAssetPage(),
  );

  const t = useTranslations();
  useTitle(t('General.assets'));
  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([breadcrumbItems.assets.listPage()]);

  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: (data: AssetPage) => (
      <PageContainer>
        <AssetTable data={data.assets} />
      </PageContainer>
    ),
  });
}
