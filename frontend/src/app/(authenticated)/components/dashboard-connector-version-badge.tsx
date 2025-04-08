/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {badgeVariants} from '@/components/ui/badge';
import {useTranslations} from 'next-intl';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import {getLocalDateTimeString, parseDateSafe} from '@/lib/utils/dates';
import React from 'react';
import {cn} from '@/lib/utils/css-utils';
import LocalTimeAgo from '@/components/local-time-ago';

export interface DashboardVersionWithTooltipProps {
  version?: string | null;
  date?: string | null;
  variant?: 'badge';
}

export default function DashboardConnectorVersionBadge(
  props: DashboardVersionWithTooltipProps,
) {
  const {version, date, variant} = props;

  const t = useTranslations();
  const parsedDate = parseDateSafe(date);
  const badgeClass = cn(
    variant === 'badge' ? badgeVariants({variant: 'outline'}) : null,
    'cursor-pointer',
  );

  return (
    <TooltipProvider>
      <Tooltip delayDuration={100}>
        <TooltipTrigger asChild>
          {<div className={badgeClass}>{version ?? t('General.unknown')}</div>}
        </TooltipTrigger>
        <TooltipContent>
          {parsedDate ? (
            <>
              <LocalTimeAgo date={parsedDate} /> ·{' '}
              {getLocalDateTimeString(parsedDate)}
            </>
          ) : (
            t('Pages.Dashboard.missingBuildInformation')
          )}
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
}
