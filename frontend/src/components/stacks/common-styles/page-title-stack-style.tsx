/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {cn} from '@/lib/utils/css-utils';
import * as React from 'react';

export default function PageTitleStackStyle(props: {
  children?: React.ReactNode;
  className?: string;
  icon: React.ReactNode;
  title: string;
  titleSiblings?: React.ReactNode;
  subtitle: React.ReactNode;
}) {
  const {children, icon, className, title, titleSiblings, subtitle} = props;

  return (
    <header className={cn('z-10 flex flex-col border-b pb-3', className)}>
      <div className="flex shrink-0 items-center justify-between gap-2">
        {icon}
        <div className="flex grow flex-col">
          <div className="flex items-center gap-2">
            <h1 className={'text-lg font-semibold'}>{title}</h1>
            {titleSiblings}
          </div>
          <div className="flex items-center gap-2 text-base text-muted-foreground">
            {subtitle}
          </div>
        </div>
        {children}
      </div>
    </header>
  );
}
