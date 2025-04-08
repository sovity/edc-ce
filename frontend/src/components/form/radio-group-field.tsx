/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import {type UiSelectItemOption} from '@/model/ui-select-item-option';
import {type Control} from 'react-hook-form';
import SelectableOption from '../dialogs/selectable-option';
import {RadioGroup, RadioGroupItem} from '../ui/radio-group';

export interface RadioGroupFieldProps {
  control: Control<any>;
  name: string;
  label: string;
  items: UiSelectItemOption[];
  disabled?: boolean;
  onChangeExec?: () => unknown;
}

const RadioGroupField = ({
  control,
  name,
  label,
  items,
  disabled,
  onChangeExec,
}: RadioGroupFieldProps) => {
  return (
    <FormField
      control={control}
      name={name}
      disabled={disabled}
      render={({field}) => (
        <FormItem>
          <FormLabel>{label}</FormLabel>
          <RadioGroup
            onValueChange={(val) => {
              field.onChange(val);
              onChangeExec?.();
            }}
            value={field.value as string}>
            {items.map(({id, label, description}) => (
              <SelectableOption
                key={id}
                htmlFor={id}
                label={label}
                description={description}>
                <RadioGroupItem
                  value={id}
                  id={id}
                  dataTestId={`radio-${name}-${id}`}
                />
              </SelectableOption>
            ))}
          </RadioGroup>
          <FormMessage />
        </FormItem>
      )}
    />
  );
};

export default RadioGroupField;
