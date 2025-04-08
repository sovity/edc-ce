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
import {useTranslations} from 'next-intl';
import ContractDetailPage from './contract-detail-page';
import PageContainer from '@/components/page-container';

export default function ContractDetailsPage({
  params,
}: {
  params: {contractId: string; tab: string};
}) {
  const {contractId, tab} = decodeParams(params);
  const t = useTranslations();

  const pageQuery = useQueryWrapper(
    queryKeys.contracts.detailsPage(contractId),
    () => api.uiApi.getContractAgreementCard({contractAgreementId: contractId}),
    {refetchInterval: 5000},
  );

  useTitle(pageQuery.data?.asset.title ?? t('General.contract'));
  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([
    breadcrumbItems.contracts.listPage(),
    breadcrumbItems.contracts.detailPage(contractId),
  ]);

  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: (data) => {
      return (
        <PageContainer>
          <ContractDetailPage initialTab={tab} contract={data} />
        </PageContainer>
      );
    },
  });
}
