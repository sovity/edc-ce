/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {CenteredLoadingSpinner} from '@/components/loading-spinner';
import PageContainer from '@/components/page-container';
import {api} from '@/lib/api/client';
import {useBreadcrumbItems} from '@/lib/breadcrumb-items';
import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useQueryWrapper} from '@/lib/hooks/use-query-wrapper';
import {useTitle} from '@/lib/hooks/use-title';
import {queryKeys} from '@/lib/queryKeys';
import {matchQueryState} from '@/lib/utils/match-query-state';
import {useTranslations} from 'next-intl';
import BusinessPartnerGroupTable from './components/business-partner-group-table';

const BusinessPartnerGroupListPage = () => {
  const pageQuery = useQueryWrapper(
    queryKeys.businessPartnerGroups.listPage(),
    () => api.uiApi.businessPartnerGroupListPage(),
  );

  const t = useTranslations();
  useTitle(t('General.businessPartnerGroups'));
  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([breadcrumbItems.businessPartnerGroups.listPage()]);
  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: (data) => (
      <PageContainer>
        <BusinessPartnerGroupTable data={data} />
      </PageContainer>
    ),
  });
};

export default BusinessPartnerGroupListPage;
