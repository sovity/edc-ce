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
import {useTranslations} from 'next-intl';
import DashboardPageContent from './components/dashboard-page-content';
import PageContainer from '@/components/page-container';

const DashboardPage = () => {
  const t = useTranslations();
  const pageQuery = useQueryWrapper(
    queryKeys.dashboard.key(),
    () => {
      return Promise.all([
        api.uiApi.getDashboardPage(),
        api.uiApi.uiConfig(),
        api.uiApi.buildInfo(),
      ]);
    },
    {refetchOnMount: 'always'},
  );

  useTitle(t('Pages.Dashboard.title'));
  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([breadcrumbItems.dashboard()]);

  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: ([data, config, buildInfo]) => (
      <PageContainer>
        <DashboardPageContent
          data={data}
          config={config}
          buildInfo={buildInfo}
        />
      </PageContainer>
    ),
  });
};

export default DashboardPage;
