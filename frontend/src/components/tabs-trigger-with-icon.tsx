/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type SvgIcon} from '@/lib/utils/svg-icon';
import {TabsTrigger} from './ui/tabs';

export interface TabsTriggerWithIconProps {
  Icon: SvgIcon;
  label: string;
  value: string;
}

const TabsTriggerWithIcon = ({
  Icon,
  label,
  value,
}: TabsTriggerWithIconProps) => {
  return (
    <TabsTrigger value={value} className="flex items-center gap-1">
      <Icon className="h-5 w-5" />
      {label}
    </TabsTrigger>
  );
};

export default TabsTriggerWithIcon;
