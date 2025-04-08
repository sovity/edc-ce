/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type ReactNode} from 'react';

export interface EntityAttributeGroupProps {
  title: string;
  children: ReactNode;
}

const EntityAttributeGroup = ({title, children}: EntityAttributeGroupProps) => {
  return (
    <div>
      <h3 className="px-1 text-[15px] font-medium text-gray-900">{title}</h3>
      <div className="mt-4 border-t border-gray-100">
        <dl className="divide-y divide-gray-100">{children}</dl>
      </div>
    </div>
  );
};

export default EntityAttributeGroup;
