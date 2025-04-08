/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import FormLabelContent from '@/components/form/form-label-content';
import {
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import {TagInput} from '@/components/ui/tag-input';
import type {Control, FieldPath, FieldValues} from 'react-hook-form';

interface TagInputFieldProps<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>,
> {
  name: TName;
  control: Control<TFieldValues>;
  label: string;
  placeholder?: string;
  description?: string;
  disabled?: boolean;
  isRequired?: boolean;
  className?: string;
  labelClassName?: string;
  tooltip?: string;
}

export function TagInputField<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>,
>({
  name,
  control,
  label,
  placeholder,
  description,
  disabled,
  className,
  tooltip,
  isRequired,
  labelClassName,
}: TagInputFieldProps<TFieldValues, TName>) {
  return (
    <FormField
      control={control}
      name={name}
      render={({field}) => {
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
              <TagInput
                tags={field.value || []}
                setTags={(tags) => field.onChange(tags)}
                placeholder={placeholder}
                disabled={disabled}
                onBlur={field.onBlur}
                name={field.name}
              />
            </FormControl>
            {description && <FormDescription>{description}</FormDescription>}
            <FormMessage />
          </FormItem>
        );
      }}
    />
  );
}
