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
import PageContainer from '@/components/page-container';
import {CreateVaultSecretForm} from './components/create-vault-secret-form';

export default function VaultSecretsListPage() {
  const pageQuery = useQueryWrapper(queryKeys.vaultSecrets.listPage(), () =>
    api.uiApi.listVaultSecretsPage(),
  );

  const t = useTranslations();
  useTitle(t('General.vaultSecrets'));
  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([breadcrumbItems.vaultSecrets.listPage()]);

  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: () => (
      <PageContainer>
        <CreateVaultSecretForm />
      </PageContainer>
    ),
  });
}
