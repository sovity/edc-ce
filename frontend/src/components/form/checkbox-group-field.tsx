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
import {cn} from '@/lib/utils/css-utils';
import {type UiSelectItemOption} from '@/model/ui-select-item-option';
import {type Control} from 'react-hook-form';
import SelectableOption from '../dialogs/selectable-option';
import {Checkbox} from '../ui/checkbox';

export interface CheckboxGroupFieldProps {
  control: Control<any, any>;
  name: string;
  label: string;
  items: UiSelectItemOption[];
  checkAlso?: Map<string, string[]>;
  className?: string;
}

const CheckboxGroupField = ({
  control,
  name,
  label,
  items,
  checkAlso,
  className,
}: CheckboxGroupFieldProps) => {
  return (
    <FormField
      control={control}
      name={name}
      render={() => (
        <FormItem>
          <FormLabel className="text-base">{label}</FormLabel>
          <div className={cn('flex flex-col gap-4 pt-2', className)}>
            {items.map((item) => (
              <SelectableOption
                key={item.id}
                htmlFor={`${name}-${item.id}`}
                label={item.label}
                description={item.description}>
                <FormField
                  control={control}
                  name={name}
                  render={({field}) => {
                    const fieldValue = field.value as string[];
                    return (
                      <FormItem
                        key={item.id}
                        className="flex flex-row items-start space-x-3 space-y-0">
                        <FormControl>
                          <Checkbox
                            id={`${name}-${item.id}`}
                            dataTestId={`form-checkbox-group-${name}-${item.id}`}
                            checked={fieldValue.includes(item.id)}
                            onCheckedChange={(checked) =>
                              checked
                                ? field.onChange([
                                    ...fieldValue,
                                    item.id,
                                    ...(checkAlso?.get(item.id) ?? []),
                                  ])
                                : field.onChange(
                                    fieldValue.filter(
                                      (value) => value !== item.id,
                                    ),
                                  )
                            }
                          />
                        </FormControl>
                      </FormItem>
                    );
                  }}
                />
              </SelectableOption>
            ))}
          </div>
          <FormMessage />
        </FormItem>
      )}
    />
  );
};

export default CheckboxGroupField;
