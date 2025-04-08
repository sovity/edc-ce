/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DateRangeInput} from '@/components/ui/date-range-input';
import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import type {Control} from 'react-hook-form';
import FormLabelContent from './form-label-content';

export interface DateRangeFieldProps {
  control: Control<any, any>;
  name: string;
  label: string;
  tooltip?: string;
  placeholder?: {
    startDate?: string;
    endDate?: string;
  };
  isRequired?: boolean;
  className?: string;
  labelClassName?: string;
}

export const DateRangeField = ({
  control,
  name,
  tooltip,
  label,
  placeholder,
  isRequired,
  className,
  labelClassName,
}: DateRangeFieldProps) => {
  return (
    <FormField
      control={control}
      name={name}
      render={({field}) => {
        // type-safety shenanigans to make the linter happy
        const value = field.value as {
          startDate?: Date | null;
          endDate?: Date | null;
        };
        const startDate = value?.startDate ?? null;
        const endDate = value?.endDate ?? null;

        return (
          <FormItem className={className}>
            <FormLabel className={labelClassName} htmlFor={name}>
              <FormLabelContent
                label={label}
                tooltip={tooltip}
                isRequired={isRequired}
              />
            </FormLabel>
            <FormControl>
              <DateRangeInput
                dataTestId={`form-date-range-${name}`}
                startDate={startDate}
                endDate={endDate}
                setStartDate={(date) => {
                  field.onChange({
                    ...value,
                    startDate: date,
                  });
                }}
                setEndDate={(date) => {
                  field.onChange({
                    ...value,
                    endDate: date,
                  });
                }}
                onBlur={field.onBlur}
                placeholder={placeholder}
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        );
      }}
    />
  );
};
