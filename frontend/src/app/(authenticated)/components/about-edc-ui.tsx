/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {urls} from '@/lib/urls';
import {useTranslations} from 'next-intl';
import {DashboardIntroChecklistItem} from './dashboard-intro-checklist-item';

export const AboutEdcUi = () => {
  const t = useTranslations();

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t('Pages.Dashboard.aboutUi')}</CardTitle>
        <CardDescription>
          {t('Pages.Dashboard.aboutUiDescription')}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <p className="mb-4 text-sm">{t('Pages.Dashboard.aboutUiIntro')}</p>
        <ul className="space-y-3">
          <DashboardIntroChecklistItem
            label={t('Pages.Dashboard.viewCatalog')}
            internalLink={urls.catalog.browserPage()}
            linkDataTestId="link-ui-intro-catalog"
            linkText={t('Pages.CatalogBrowser.title')}
          />
          <DashboardIntroChecklistItem
            label={t('Pages.Dashboard.negotiateContract')}
            internalLink={urls.catalog.browserPage()}
            linkDataTestId="link-ui-intro-catalog2"
            linkText={t('Pages.CatalogBrowser.title')}
          />
          <DashboardIntroChecklistItem
            label={t('Pages.Dashboard.viewContracts')}
            internalLink={urls.contracts.listPage()}
            linkDataTestId="link-ui-intro-contracts"
            linkText={t('General.contractPage')}
          />
          <DashboardIntroChecklistItem
            label={t('Pages.Dashboard.transferAsset')}
            internalLink={urls.contracts.listPage()}
            linkDataTestId="link-ui-intro-contracts2"
            linkText={t('General.contractPage')}
          />
          <DashboardIntroChecklistItem
            label={t('Pages.Dashboard.viewTransferHistory')}
            internalLink={urls.transferHistory()}
            linkDataTestId="link-ui-intro-transfer"
            linkText={t('General.transferHistoryPage')}
          />
          <DashboardIntroChecklistItem
            label={t('Pages.Dashboard.viewAndCreateAssets')}
            internalLink={urls.assets.listPage()}
            linkDataTestId="link-ui-intro-assets"
            linkText={t('General.assetPage')}
          />
          <DashboardIntroChecklistItem
            label={t('Pages.Dashboard.viewAndCreatePolicies')}
            internalLink={urls.policies.listPage()}
            linkDataTestId="link-ui-intro-policies"
            linkText={t('General.policyPage')}
          />
          <DashboardIntroChecklistItem
            label={t('Pages.Dashboard.publishAsset')}
            internalLink={urls.dataOffers.listPage()}
            linkDataTestId="link-ui-intro-data-offers"
            linkText={t('General.dataOfferPage')}
          />
        </ul>
      </CardContent>
    </Card>
  );
};
