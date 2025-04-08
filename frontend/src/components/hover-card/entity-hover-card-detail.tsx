/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type SvgIcon} from '@/lib/utils/svg-icon';

export interface HoverCardDetailProps {
  Icon: SvgIcon;
  label: string | React.ReactElement;
  value?: string | React.ReactNode;
}

const EntityHoverCardDetail = ({Icon, label, value}: HoverCardDetailProps) => {
  return (
    <>
      <div className="col-span-2">
        <div className="flex items-center gap-2 text-gray-600">
          <Icon className="h-4 w-4" />
          <span>{label}</span>
        </div>
      </div>
      <span className="col-span-3 break-words">{value}</span>
    </>
  );
};

export default EntityHoverCardDetail;
