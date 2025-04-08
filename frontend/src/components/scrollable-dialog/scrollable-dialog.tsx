/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type ReactNode} from 'react';
import {
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {ScrollArea} from '../ui/scroll-area';

export interface ScrollableDialogProps {
  title: string | ReactNode;
  subtitle?: string;
  content: string | ReactNode;
}

const ScrollableDialog = ({
  title,
  subtitle,
  content,
}: ScrollableDialogProps) => {
  return (
    <>
      <DialogHeader>
        <DialogTitle>
          <div>
            <h3 className="pr-5 text-base font-semibold text-brand-darkblue">
              {title}
            </h3>
            {subtitle && (
              <div className="text-sm text-muted-foreground">{subtitle}</div>
            )}
          </div>
        </DialogTitle>
      </DialogHeader>
      <DialogDescription asChild>
        <div className="px-1">
          <ScrollArea className="pr-3 text-gray-700">
            <div className="max-h-[30rem]">
              {typeof content === 'string' ? (
                <div className="whitespace-pre-line">{content}</div>
              ) : (
                content
              )}
            </div>
          </ScrollArea>
        </div>
      </DialogDescription>
    </>
  );
};

export default ScrollableDialog;
