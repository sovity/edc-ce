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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {type UiSelectItemOption} from '@/model/ui-select-item-option';
import {type Control} from 'react-hook-form';
import FormLabelContent from './form-label-content';

export interface SelectFieldProps {
  control: Control<any, any>;
  name: string;
  label: string;
  tooltip?: string;
  items: UiSelectItemOption[];
  placeholder: string;
  isRequired?: boolean;
  onValueChange?: (event: string) => void;
  className?: string;
}

const SelectField = ({
  control,
  name,
  label,
  tooltip,
  placeholder,
  items,
  isRequired,
  onValueChange,
  className,
}: SelectFieldProps) => {
  return (
    <FormField
      control={control}
      name={name}
      render={({field}) => (
        <FormItem className={className}>
          <FormLabel>
            <FormLabelContent
              label={label}
              tooltip={tooltip}
              isRequired={isRequired}
            />
          </FormLabel>
          <Select
            data-testid={`form-field-${name}`}
            onValueChange={onValueChange ?? field.onChange}
            value={field.value as string}>
            <FormControl>
              <SelectTrigger data-testid={`form-select-${name}-trigger`}>
                <SelectValue placeholder={placeholder} />
              </SelectTrigger>
            </FormControl>
            <SelectContent className="max-h-64">
              {items.map((item) => (
                <SelectItem
                  key={item.id}
                  value={item.id}
                  data-testid={`form-select-${name}-item-${item.id}`}>
                  <span>{item.label}</span>
                  {item.description && (
                    <span className="text-xs text-muted-foreground">
                      &nbsp;{item.description}
                    </span>
                  )}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <FormMessage />
        </FormItem>
      )}
    />
  );
};

export default SelectField;
