/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {CenteredLoadingSpinner} from '@/components/loading-spinner';
import {InitiateTransferForm} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-form';
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

export default function ContractTransferPage({
  params,
}: {
  params: {contractId: string};
}) {
  const breadcrumbItems = useBreadcrumbItems();
  const t = useTranslations();
  const {contractId} = decodeParams(params);

  const pageQuery = useQueryWrapper(queryKeys.contracts.id(contractId), () =>
    api.uiApi.getContractAgreementCard({contractAgreementId: contractId}),
  );

  useTitle(t('Pages.InitiateTransfer.title'));
  useBreadcrumbs([
    breadcrumbItems.contracts.listPage(),
    breadcrumbItems.contracts.detailPage(contractId),
    breadcrumbItems.contracts.transferPage(contractId),
  ]);

  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: (data) => {
      return (
        <PageContainer>
          <InitiateTransferForm contractAgreement={data} />
        </PageContainer>
      );
    },
  });
}
