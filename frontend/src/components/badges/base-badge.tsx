/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {cn} from '@/lib/utils/css-utils';

interface BaseBadgeProps {
  classes: string;
  label: string;
}

const BaseBadge = ({label, classes}: BaseBadgeProps) => {
  return (
    <div
      className={cn(
        'inline-flex items-center justify-center rounded-md',
        'px-2 py-1 text-xs font-medium',
        'ring-1 ring-inset',
        'whitespace-nowrap',
        classes,
      )}>
      {label}
    </div>
  );
};

export default BaseBadge;
