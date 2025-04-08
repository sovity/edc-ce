/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {CenteredLoadingSpinner} from '@/components/loading-spinner';
import CatalogDataOfferTable from '@/app/(authenticated)/catalog/[participantId]/[endpointUrl]/components/catalog-data-offer-table';
import {api} from '@/lib/api/client';
import {useBreadcrumbItems} from '@/lib/breadcrumb-items';
import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useQueryWrapper} from '@/lib/hooks/use-query-wrapper';
import {useTitle} from '@/lib/hooks/use-title';
import {matchQueryState} from '@/lib/utils/match-query-state';
import {queryKeys} from '@/lib/queryKeys';
import {decodeParams} from '@/lib/utils/http-utils';
import {useTranslations} from 'next-intl';
import PageContainer from '@/components/page-container';

const CatalogResultPage = ({
  params,
}: {
  params: {participantId: string; endpointUrl: string};
}) => {
  const {participantId, endpointUrl} = decodeParams(params);

  const pageQuery = useQueryWrapper(
    queryKeys.catalog.listPage(participantId, endpointUrl),
    () =>
      api.uiApi.getCatalogPageDataOffers({
        participantId,
        connectorEndpoint: endpointUrl,
      }),
  );

  const t = useTranslations();
  const breadcrumbItems = useBreadcrumbItems();

  useTitle(t('Pages.CatalogBrowser.title'));
  useBreadcrumbs([
    breadcrumbItems.catalog.home(),
    breadcrumbItems.catalog.dataOfferList(participantId, endpointUrl),
  ]);

  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: (data) => (
      <PageContainer>
        <CatalogDataOfferTable data={data} />
      </PageContainer>
    ),
  });
};

export default CatalogResultPage;
