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
import {Textarea} from '@/components/ui/textarea';
import FormLabelContent from './form-label-content';
import {type TextFieldProps} from './input-field';

const TextareaField = ({
  control,
  name,
  tooltip,
  label,
  placeholder,
  isRequired,
}: TextFieldProps) => {
  return (
    <FormField
      control={control}
      name={name}
      render={({field}) => (
        <FormItem>
          <FormLabel htmlFor={name}>
            <FormLabelContent
              label={label}
              tooltip={tooltip}
              isRequired={isRequired}
            />
          </FormLabel>
          <FormControl>
            <Textarea
              data-testid={`form-textarea-${name}`}
              id={name}
              placeholder={placeholder}
              className={'min-h-48'}
              {...field}
            />
          </FormControl>
          <FormMessage />
        </FormItem>
      )}
    />
  );
};

export default TextareaField;
