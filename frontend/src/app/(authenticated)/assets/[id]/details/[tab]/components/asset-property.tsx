/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type SvgIcon} from '@/lib/utils/svg-icon';
import {cn} from '@/lib/utils/css-utils';

export interface AssetPropertyProps {
  Icon: SvgIcon;
  label: string;
  value?: string | React.ReactNode;
  className?: string;
}

export const AssetProperty = ({
  Icon,
  label,
  value,
  className,
}: AssetPropertyProps) => {
  return value ? (
    <div className={cn('flex w-full gap-3 rounded-lg p-3', className)}>
      <Icon className="mt-0.5 h-5 w-5 shrink-0 text-muted-foreground" />
      <div className="flex grow flex-col">
        <div className="text-xs text-muted-foreground">{label}</div>
        <div className="text-sm font-medium">{value}</div>
      </div>
    </div>
  ) : null;
};
