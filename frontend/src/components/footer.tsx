/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useConfig} from '@/lib/hooks/use-config';
import ExternalLink from './links/external-link';
import {useTranslations} from 'next-intl';

export const Footer = () => {
  const config = useConfig();
  const t = useTranslations();
  const current_year = new Date().getFullYear();
  return (
    <footer className="flex flex-col gap-4 py-4 text-center text-xs">
      {(config?.logoutUrl || config?.documentationUrl) && (
        <div className="flex flex-col gap-2">
          {config?.supportUrl && (
            <ExternalLink
              href={config.supportUrl}
              dataTestId={'footer-button-documentation'}
              variant={'default'}
              size={'sm'}>
              {t('General.supportLinkText')}
            </ExternalLink>
          )}
          {config?.documentationUrl && (
            <ExternalLink
              href={config.documentationUrl}
              dataTestId={'footer-button-documentation'}
              variant={'default'}
              size={'sm'}>
              {t('General.documentationLinkText')}
            </ExternalLink>
          )}
        </div>
      )}
      <div className="flex flex-col gap-2">
        {(config?.privacyPolicyUrl || config?.legalNoticeUrl) && (
          <div className="flex items-center justify-center gap-2">
            {config?.legalNoticeUrl && (
              <ExternalLink
                href={config.legalNoticeUrl}
                className="text-xs"
                noIcon
                showReferrer>
                {t('General.legalNotice')}
              </ExternalLink>
            )}
            {config?.privacyPolicyUrl && (
              <ExternalLink
                href={config.privacyPolicyUrl}
                className="text-xs"
                noIcon
                showReferrer>
                {t('General.privacyPolicy')}
              </ExternalLink>
            )}
          </div>
        )}
        <p>© {current_year} sovity GmbH</p>
      </div>
    </footer>
  );
};
