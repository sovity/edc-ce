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
import {CreateBusinessPartnerGroupForm} from './components/create-business-partner-group-form';

export default function BusinessPartnerGroupCreatePage() {
  const t = useTranslations();
  useTitle(t('Pages.BusinessPartnerGroupsCreateForm.title'));
  const breadcrumbItems = useBreadcrumbItems();
  useBreadcrumbs([
    breadcrumbItems.businessPartnerGroups.listPage(),
    breadcrumbItems.businessPartnerGroups.createPage(),
  ]);

  return (
    <PageContainer>
      <CreateBusinessPartnerGroupForm />
    </PageContainer>
  );
}
