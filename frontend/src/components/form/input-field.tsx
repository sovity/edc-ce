/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import {Input} from '@/components/ui/input';
import {type Control} from 'react-hook-form';
import FormLabelContent from './form-label-content';
import {type HTMLInputTypeAttribute} from 'react';

export interface TextFieldProps {
  control: Control<any, any>;
  name: string;
  label: string;
  tooltip?: string;
  placeholder?: string;
  type?: HTMLInputTypeAttribute;
  value?: string;
  disableAutoComplete?: boolean;
  labelClassName?: string;
  isRequired?: boolean;
  children?: React.ReactNode;
  disabled?: boolean;
  className?: string;
}

const InputField = ({
  control,
  name,
  label,
  tooltip,
  placeholder,
  type,
  disableAutoComplete,
  isRequired,
  labelClassName,
  children,
  disabled,
  className,
  value,
}: TextFieldProps) => {
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
            <div className="relative">
              {children}
              <Input
                id={name}
                data-testid={`form-input-${name}`}
                placeholder={placeholder}
                {...field}
                value={typeof field.value === 'string' ? field.value : value}
                type={type ?? 'text'}
                autoComplete={disableAutoComplete ? 'off' : undefined}
                disabled={disabled}
              />
            </div>
          </FormControl>
          <FormMessage />
        </FormItem>
      )}
    />
  );
};

export default InputField;
