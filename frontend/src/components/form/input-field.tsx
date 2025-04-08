/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {Button} from '@/components/ui/button';
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

export interface TextFieldProps {
  control: Control<any, any>;
  name: string;
  label: string;
  tooltip?: string;
  placeholder?: string;
  number?: boolean;
  value?: string;
  icon?: React.ReactNode;
  onIconClick?: () => void;
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
  number,
  icon,
  onIconClick,
  disableAutoComplete,
  isRequired,
  labelClassName,
  children,
  disabled,
  className,
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
                type={number ? 'number' : 'text'}
                autoComplete={disableAutoComplete ? 'off' : undefined}
                disabled={disabled}
              />
              {icon && (
                <div className="absolute inset-y-0 right-0 flex items-center pr-2">
                  <Button
                    dataTestId={`form-input-${name}-icon-btn`}
                    type="button"
                    size="smallIcon"
                    variant="ghost"
                    onClick={onIconClick}>
                    <div>{icon}</div>
                  </Button>
                </div>
              )}
            </div>
          </FormControl>
          <FormMessage />
        </FormItem>
      )}
    />
  );
};

export default InputField;
