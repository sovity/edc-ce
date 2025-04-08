/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {PolicyCreateForm} from '@/app/(authenticated)/policies/create/components/policy-create-form';
import {useBreadcrumbItems} from '@/lib/breadcrumb-items';
import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useTitle} from '@/lib/hooks/use-title';
import {useTranslations} from 'next-intl';
import PageContainer from '@/components/page-container';

const PolicyCreatePage = () => {
  const breadcrumbItems = useBreadcrumbItems();
  const t = useTranslations();

  useTitle(t('Pages.PolicyCreate.title'));
  useBreadcrumbs([
    breadcrumbItems.policies.listPage(),
    breadcrumbItems.policies.createPage(),
  ]);

  return (
    <PageContainer>
      <PolicyCreateForm />
    </PageContainer>
  );
};
export default PolicyCreatePage;
