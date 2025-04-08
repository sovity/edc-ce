/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {CenteredLoadingSpinner} from '@/components/loading-spinner';
import CatalogBrowserForm from '@/app/(authenticated)/catalog/components/catalog-browser-form';
import {useBreadcrumbItems} from '@/lib/breadcrumb-items';
import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useConfig} from '@/lib/hooks/use-config';
import {useTitle} from '@/lib/hooks/use-title';
import {useTranslations} from 'next-intl';
import PageContainer from '@/components/page-container';

const CatalogBrowserLandingPage = () => {
  const t = useTranslations();
  const breadcrumbItems = useBreadcrumbItems();
  const config = useConfig();

  useTitle(t('Pages.CatalogBrowser.title'));
  useBreadcrumbs([breadcrumbItems.catalog.home()]);

  return config ? (
    <PageContainer>
      <CatalogBrowserForm
        preconfiguredCounterParties={config?.preconfiguredCounterparties ?? []}
      />
    </PageContainer>
  ) : (
    <CenteredLoadingSpinner />
  );
};

export default CatalogBrowserLandingPage;
