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
import Link from 'next/link';

export default function TableCellStackStyle(props: {
  href: string;
  dataTestId: string;
  children?: React.ReactNode;
  className?: string;
  icon: React.ReactNode;
  title: string;
  titleSiblings?: React.ReactNode;
  subtitle: React.ReactNode;
}) {
  const {
    href,
    dataTestId,
    children,
    className,
    icon,
    title,
    titleSiblings,
    subtitle,
  } = props;

  return (
    <Link
      data-testid={dataTestId}
      href={href}
      className={cn(
        'flex flex-row items-center justify-start gap-3',
        className,
      )}>
      {icon}
      <div className="flex flex-col items-start justify-start">
        <div className="flex items-center gap-2">
          <span>{title}</span>
          {titleSiblings}
        </div>
        <span className="flex items-center gap-2 text-sm text-muted-foreground">
          {subtitle}
        </span>
      </div>
      {children}
    </Link>
  );
}
