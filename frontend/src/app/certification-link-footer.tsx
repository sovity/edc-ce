/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import ExternalLink from '@/components/links/external-link';
import {cn} from '@/lib/utils/css-utils';
import {type UiConfig} from '@sovity.de/edc-client';

const CertificationLinkFooter = ({
  config,
  className,
}: {
  config?: UiConfig;
  className?: string;
}) => {
  const icpFooterLinkUrl = config?.icpFooterLinkUrl;
  const icpFooterLinkText = config?.icpFooterLinkText;

  return icpFooterLinkText && icpFooterLinkUrl ? (
    <div className={cn('w-full text-center', className)}>
      <ExternalLink
        href={icpFooterLinkUrl}
        className="text-xs"
        noIcon
        showReferrer>
        {icpFooterLinkText}
      </ExternalLink>
    </div>
  ) : null;
};

export default CertificationLinkFooter;
