/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {CenteredLoadingSpinner} from '@/components/loading-spinner';
import {sovityDataspacePolicyContext} from '@/components/policy-editor/supported-policies';
import {buildPolicyDefinitionMapped} from '@/app/(authenticated)/policies/components/policy-definition-mapped';
import PolicyDefinitionTable from '@/app/(authenticated)/policies/components/policy-definition-table';
import {api} from '@/lib/api/client';
import {useBreadcrumbItems} from '@/lib/breadcrumb-items';
import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useQueryWrapper} from '@/lib/hooks/use-query-wrapper';
import {useTitle} from '@/lib/hooks/use-title';
import {matchQueryState} from '@/lib/utils/match-query-state';
import {queryKeys} from '@/lib/queryKeys';
import {useTranslations} from 'next-intl';
import PageContainer from '@/components/page-container';

const PolicyListPage = () => {
  const pageQuery = useQueryWrapper(queryKeys.policies.listPage(), () =>
    api.uiApi.getPolicyDefinitionPage(),
  );

  const t = useTranslations();
  useTitle(t('Pages.PolicyList.title'));
  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([breadcrumbItems.policies.listPage()]);

  // Supported Policies
  const policyContext = sovityDataspacePolicyContext();

  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: (data) => {
      const policyDefinitions = data.policies.map((it) =>
        buildPolicyDefinitionMapped(it, policyContext),
      );
      return (
        <PageContainer>
          <PolicyDefinitionTable data={policyDefinitions} />
        </PageContainer>
      );
    },
  });
};
export default PolicyListPage;
