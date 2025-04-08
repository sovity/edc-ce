/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {cn} from '@/lib/utils/css-utils';

interface StatusBadgeProps {
  classes: string;
  label: string;
  dataTestId?: string;
  maxWidth?: string;
}

const StatusBadge = ({
  label,
  classes,
  dataTestId,
  maxWidth,
}: StatusBadgeProps) => {
  return (
    <div
      className={cn(
        'inline-flex items-center justify-center rounded-md',
        'px-2 py-1 text-xs font-medium',
        'ring-1 ring-inset',
        'whitespace-nowrap',
        maxWidth,
        classes,
      )}
      data-testid={dataTestId}>
      {label}
    </div>
  );
};

export default StatusBadge;
