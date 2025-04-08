/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DateInput} from '@/components/ui/date-input';
import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import type {Control} from 'react-hook-form';
import FormLabelContent from './form-label-content';

export interface DateFieldProps {
  control: Control<any, any>;
  name: string;
  label: string;
  tooltip?: string;
  placeholder?: string;
  isRequired?: boolean;
  className?: string;
  labelClassName?: string;
}

export const DateField = ({
  control,
  name,
  tooltip,
  label,
  placeholder,
  isRequired,
  className,
  labelClassName,
}: DateFieldProps) => {
  return (
    <FormField
      control={control}
      name={name}
      render={({field}) => (
        <FormItem className={className}>
          <FormLabel className={labelClassName} htmlFor={name}>
            <FormLabelContent
              label={label}
              tooltip={tooltip}
              isRequired={isRequired}
            />
          </FormLabel>
          <FormControl>
            <DateInput
              dataTestId={`form-date-input-${name}`}
              placeholder={placeholder}
              date={field.value as Date | null}
              setDate={(date) => field.onChange(date)}
              onBlur={field.onBlur}
            />
          </FormControl>
          <FormMessage />
        </FormItem>
      )}
    />
  );
};
