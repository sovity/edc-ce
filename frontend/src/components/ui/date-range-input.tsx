/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DateInput} from '@/components/ui/date-input';
import {cn} from '@/lib/utils/css-utils';

export interface DateRangeInputProps {
  startDate: Date | null;
  endDate: Date | null;
  setStartDate: (date: Date | null) => void;
  setEndDate: (date: Date | null) => void;
  onBlur?: () => void;
  placeholder?: {
    startDate?: string;
    endDate?: string;
  };
  className?: string;
  dataTestId: string;
}

export function DateRangeInput({
  startDate,
  endDate,
  setStartDate,
  setEndDate,
  onBlur,
  placeholder,
  className,
  dataTestId,
}: DateRangeInputProps) {
  return (
    <div
      className={cn(
        'flex flex-col justify-between gap-2 sm:flex-row',
        className,
      )}>
      <div className="flex grow">
        <DateInput
          dataTestId={`${dataTestId}-start`}
          date={startDate}
          setDate={setStartDate}
          onBlur={onBlur}
          placeholder={placeholder?.startDate || 'Start date'}
          className={'flex grow'}
        />
      </div>
      <div className="flex items-center justify-center">
        <span className="text-muted-foreground">to</span>
      </div>
      <div className="flex grow">
        <DateInput
          dataTestId={`${dataTestId}-start`}
          date={endDate}
          setDate={setEndDate}
          onBlur={onBlur}
          placeholder={placeholder?.endDate || 'End date'}
          className={'flex grow'}
        />
      </div>
    </div>
  );
}
