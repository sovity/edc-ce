/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import * as React from 'react';
import {cn} from '@/lib/utils/css-utils';

/**
 * Renders:
 *  - header row
 *  - children as rows: indented with tree-like lines
 */
export const TreeLike = ({
  childContainerClassName,
  header,
  children,
}: {
  childContainerClassName?: string;
  header: React.ReactNode;
  children: React.ReactNode;
}) => {
  const numChildren = React.Children.count(children) - 1;
  return (
    <div className={cn('flex flex-col gap-1')}>
      {header}
      <div
        className={cn(
          'ml-2 flex flex-col gap-1 border-0 border-l-2 border-solid border-gray-500 pl-4',
          childContainerClassName,
        )}>
        {React.Children.map(children, (child, iChild) => {
          const isLast = iChild === numChildren;
          return (
            <div key={iChild} className="flex">
              <span
                className={cn(
                  'mt-[0.6rem] w-2 border-0 border-t-2 border-solid border-gray-500',
                  {'bg-white': isLast},
                )}
                style={{
                  marginLeft: 'calc(-1rem - 2px)',
                  marginRight: 'calc(0.5rem + 2px)',
                }}
              />
              {child}
            </div>
          );
        })}
      </div>
    </div>
  );
};
