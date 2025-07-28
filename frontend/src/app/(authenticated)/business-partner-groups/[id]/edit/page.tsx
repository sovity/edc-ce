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
import {EditBusinessPartnerGroupForm} from './components/edit-business-partner-group-form';
import {useTranslations} from 'next-intl';

const BusinessPartnerGroupEditPage = ({
  params: {id},
}: {
  params: {id: string};
}) => {
  const t = useTranslations();
  const pageQuery = useQueryWrapper(
    queryKeys.businessPartnerGroups.editPage(id),
    () => api.uiApi.businessPartnerGroupEditPage({id}),
  );
  useTitle(`${t('General.edit')} ${id}`);
  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([
    breadcrumbItems.businessPartnerGroups.listPage(),
    breadcrumbItems.businessPartnerGroups.detailPage(id),
    breadcrumbItems.businessPartnerGroups.editPage(id),
  ]);
  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: (data) => (
      <PageContainer>
        <EditBusinessPartnerGroupForm data={data} />
      </PageContainer>
    ),
  });
};

export default BusinessPartnerGroupEditPage;
