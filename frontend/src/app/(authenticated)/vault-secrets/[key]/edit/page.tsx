/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {CenteredLoadingSpinner} from '@/components/loading-spinner';
import {useBreadcrumbItems} from '@/lib/breadcrumb-items';
import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useQueryWrapper} from '@/lib/hooks/use-query-wrapper';
import {useTitle} from '@/lib/hooks/use-title';
import {matchQueryState} from '@/lib/utils/match-query-state';
import {queryKeys} from '@/lib/queryKeys';
import {useTranslations} from 'next-intl';
import PageContainer from '@/components/page-container';
import {EditVaultSecretForm} from './components/edit-vault-secret-form';
import {api} from '@/lib/api/client';
import {decodeParams} from '@/lib/utils/http-utils';

export default function EditVaultSecretPage({params}: {params: {key: string}}) {
  const {key} = decodeParams(params);
  const pageQuery = useQueryWrapper(queryKeys.vaultSecrets.id(key), () =>
    api.uiApi.editVaultSecretPage({key}),
  );

  const t = useTranslations();
  useTitle(t('General.editVaultSecret'));
  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([
    breadcrumbItems.vaultSecrets.listPage(),
    breadcrumbItems.vaultSecrets.detailPage(key),
    breadcrumbItems.vaultSecrets.editPage(key),
  ]);

  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: (data) => (
      <PageContainer>
        <EditVaultSecretForm id={key} data={data} />
      </PageContainer>
    ),
  });
}
