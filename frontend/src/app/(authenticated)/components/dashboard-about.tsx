/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import ExternalLink from '@/components/links/external-link';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {cn} from '@/lib/utils/css-utils';
import {Github} from 'lucide-react';
import {useTranslations} from 'next-intl';
import {AboutEdcUi} from './about-edc-ui';

export interface DashboardAboutProps {
  showMarketingCard: boolean;
}

export const DashboardAbout = (props: DashboardAboutProps) => {
  const {showMarketingCard} = props;
  const t = useTranslations();

  return (
    <>
      <div
        className={cn('grid grid-cols-1 gap-6 md:grid-cols-2', {
          'mb-6': showMarketingCard,
        })}>
        <Card>
          <CardHeader>
            <CardTitle>{t('Pages.Dashboard.aboutEdc')}</CardTitle>
            <CardDescription>
              {t('Pages.Dashboard.aboutEdcDescription')}
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <p className="text-sm">{t('Pages.Dashboard.aboutEdcText')}</p>
            <p className="text-sm">{t('Pages.Dashboard.aboutEdcText2')}</p>
            <p className="text-sm">{t('Pages.Dashboard.aboutEdcText3')}</p>
            <div className="mt-4 flex justify-end">
              <ExternalLink
                href="https://github.com/eclipse-dataspaceconnector/DataSpaceConnector"
                variant="outline"
                size="default"
                noIcon>
                <div className="flex items-center gap-1">
                  <Github className="h-4 w-4" />
                  <div>GitHub</div>
                </div>
              </ExternalLink>
            </div>
          </CardContent>
        </Card>

        {showMarketingCard ? (
          <Card>
            <CardHeader>
              <CardTitle>{t('Pages.Dashboard.getManagedEdc')}</CardTitle>
              <CardDescription>
                {t('Pages.Dashboard.getManagedEdcDescription')}
              </CardDescription>
            </CardHeader>
            <CardContent className="flex flex-col justify-between gap-4">
              <p className="text-sm">
                {t('Pages.Dashboard.getManagedEdcText')}
              </p>
              <p className="text-sm">
                {t('Pages.Dashboard.getManagedEdcText2')}
              </p>
              <div className="mt-10 flex justify-end gap-3">
                <ExternalLink
                  href="https://sovity.de"
                  variant="outline"
                  size="default"
                  showReferrer>
                  sovity
                </ExternalLink>
                <ExternalLink
                  href="mailto:contact@sovity.de"
                  variant="default"
                  size="default"
                  noIcon>
                  {t('General.contact')}
                </ExternalLink>
              </div>
            </CardContent>
          </Card>
        ) : (
          <AboutEdcUi />
        )}
      </div>
      {showMarketingCard && <AboutEdcUi />}
    </>
  );
};
