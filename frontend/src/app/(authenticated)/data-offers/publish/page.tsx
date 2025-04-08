/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {CenteredLoadingSpinner} from '@/components/loading-spinner';
import PublishDataOfferForm from '@/app/(authenticated)/data-offers/publish/components/publish-data-offer-form';
import {api} from '@/lib/api/client';
import {useBreadcrumbItems} from '@/lib/breadcrumb-items';
import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useQueryWrapper} from '@/lib/hooks/use-query-wrapper';
import {useTitle} from '@/lib/hooks/use-title';
import {matchQueryState} from '@/lib/utils/match-query-state';
import {queryKeys} from '@/lib/queryKeys';
import {useTranslations} from 'next-intl';
import PageContainer from '@/components/page-container';

const PublishDataOfferPage = () => {
  const pageQuery = useQueryWrapper(queryKeys.assets.listPage(), () => {
    return Promise.all([
      api.uiApi.getAssetPage(),
      api.uiApi.getPolicyDefinitionPage(),
    ]);
  });

  const t = useTranslations();
  useTitle(t('Pages.PublishDataOffer.title'));

  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([
    breadcrumbItems.dataOffers.listPage(),
    breadcrumbItems.dataOffers.publishPage(),
  ]);

  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: ([assets, policies]) => {
      return (
        <PageContainer>
          <PublishDataOfferForm
            assets={assets.assets ?? []}
            policies={policies.policies ?? []}
          />
        </PageContainer>
      );
    },
  });
};

export default PublishDataOfferPage;
