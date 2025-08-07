/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useBreadcrumbItems} from '@/lib/breadcrumb-items';
import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useTitle} from '@/lib/hooks/use-title';
import {useTranslations} from 'next-intl';
import PageContainer from '@/components/page-container';
import {CreateVaultSecretForm} from './components/create-vault-secret-form';

export default function VaultSecretCreatePage() {
  const t = useTranslations();
  useTitle(t('General.vaultSecrets'));
  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([
    breadcrumbItems.vaultSecrets.listPage(),
    breadcrumbItems.vaultSecrets.createPage(),
  ]);

  return (
    <PageContainer>
      <CreateVaultSecretForm />
    </PageContainer>
  );
}
