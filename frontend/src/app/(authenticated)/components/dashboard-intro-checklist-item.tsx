/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import InternalLink from '@/components/links/internal-link';
import {CheckIcon} from 'lucide-react';

export interface DashboardIntroChecklistItemProps {
  label: string;
  internalLink: string;
  linkDataTestId: string;
  linkText: string;
}

export const DashboardIntroChecklistItem = (
  props: DashboardIntroChecklistItemProps,
) => {
  const {label, internalLink, linkText, linkDataTestId} = props;

  return (
    <li className="flex gap-2">
      <CheckIcon className="mt-0.5 h-4 w-4" />
      <p className="text-sm">
        {label}{' '}
        <InternalLink
          dataTestId={linkDataTestId}
          href={internalLink}
          variant="link"
          size="fit">
          {linkText}
        </InternalLink>
      </p>
    </li>
  );
};
