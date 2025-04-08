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
import {decodeParams} from '@/lib/utils/http-utils';
import {throwIfNull} from '@/lib/utils/throw-utils';
import {useTranslations} from 'next-intl';
import CatalogDataOfferDetailPageContent from './catalog-data-offer-detail-page';
import PageContainer from '@/components/page-container';

export default function CatalogDataOfferDetailPage({
  params,
}: {
  params: {
    participantId: string;
    endpointUrl: string;
    assetId: string;
    tab: string;
  };
}) {
  const t = useTranslations();
  const breadcrumbItems = useBreadcrumbItems();
  const {participantId, endpointUrl, assetId} = decodeParams(params);

  const pageQuery = useQueryWrapper(
    queryKeys.catalog.dataOfferDetails(participantId, endpointUrl, assetId),
    () =>
      api.uiApi
        .getCatalogPageDataOffers({
          connectorEndpoint: endpointUrl,
          participantId: participantId,
        })
        .then((data) =>
          data?.find(
            (data) =>
              data.endpoint === endpointUrl &&
              data.participantId === participantId &&
              data.asset.assetId === assetId,
          ),
        )
        .then((asset) =>
          throwIfNull(asset, t('Pages.CatalogDataOfferDetails.notFound')),
        ),
  );

  useTitle(`${pageQuery.data?.asset.title ?? assetId} | Catalog Data Offer`);
  useBreadcrumbs([
    breadcrumbItems.catalog.home(),
    breadcrumbItems.catalog.dataOfferList(participantId, endpointUrl),
    breadcrumbItems.catalog.dataOfferDetail(
      participantId,
      endpointUrl,
      assetId,
    ),
  ]);

  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: (data) => (
      <PageContainer>
        <CatalogDataOfferDetailPageContent
          data={data}
          initialTab={params.tab}
        />
      </PageContainer>
    ),
  });
}
