/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import * as React from 'react';
import {Button} from '@/components/ui/button';
import {Calendar} from '@/components/ui/calendar';
import {Popover, PopoverContent, PopoverTrigger} from '@/components/ui/popover';
import {cn} from '@/lib/utils/css-utils';
import {getLocalDateString} from '@/lib/utils/dates';
import {CalendarIcon} from 'lucide-react';

export type DateInputProps = {
  date: Date | null;
  setDate: (date: Date | null) => void;
  onBlur?: () => void;
  dataTestId: string;
  className?: string;
  placeholder?: string;
};

export const DateInput = ({
  date,
  setDate,
  dataTestId,
  className,
  placeholder,
  onBlur,
}: DateInputProps) => {
  return (
    <Popover
      onOpenChange={(open) => {
        if (!open) {
          onBlur?.();
        }
      }}>
      <PopoverTrigger asChild>
        <Button
          dataTestId={dataTestId}
          variant={'outline'}
          className={cn(
            'flex justify-start gap-2 text-left font-normal',
            !date && 'text-muted-foreground',
            className,
          )}>
          <CalendarIcon />
          {date ? (
            getLocalDateString(date)
          ) : (
            <span>{placeholder ?? 'Pick a date'}</span>
          )}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-0" align="start">
        <Calendar
          mode="single"
          selected={date ?? undefined}
          onSelect={(day) => setDate(day ?? null)}
          initialFocus
        />
      </PopoverContent>
    </Popover>
  );
};
