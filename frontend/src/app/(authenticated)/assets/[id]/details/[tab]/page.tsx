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
import {throwIfNull} from '@/lib/utils/throw-utils';
import {useTranslations} from 'next-intl';
import AssetDetailPageContent from './components/asset-detail-page-content';
import PageContainer from '@/components/page-container';

export default function AssetDetailsPage({
  params,
}: {
  params: {id: string; tab: string};
}) {
  const {id, tab} = params;
  const t = useTranslations();

  const pageQuery = useQueryWrapper(queryKeys.assets.detailsPage(id), () =>
    api.uiApi
      .getAssetPage()
      .then((data) => data.assets?.find((asset) => asset.assetId === id))
      .then((asset) => throwIfNull(asset, t('Pages.AssetDetails.notFound'))),
  );

  useTitle(pageQuery.data?.title ?? id);
  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([
    breadcrumbItems.assets.listPage(),
    breadcrumbItems.assets.detailPage(id, pageQuery.data?.title),
  ]);

  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: (data) => {
      return (
        <PageContainer>
          <AssetDetailPageContent asset={data} initialTab={tab} />
        </PageContainer>
      );
    },
  });
}
