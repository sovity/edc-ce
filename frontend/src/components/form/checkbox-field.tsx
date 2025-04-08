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

export interface CheckboxFieldProps {
  control: Control<any, any>;
  name: string;
  label: string;
  tooltip?: string;
  isRequired?: boolean;
  labelClassName?: string;
  item: UiSelectItemOption;
  checkAlso?: Map<string, string[]>;
  className?: string;
}

const CheckboxField = ({
  control,
  name,
  label,
  item,
  tooltip,
  isRequired,
  labelClassName,
  className,
}: CheckboxFieldProps) => {
  return (
    <FormField
      control={control}
      name={name}
      render={() => (
        <FormItem>
          <FormLabel className={labelClassName} htmlFor={name}>
            <FormLabelContent
              label={label}
              tooltip={tooltip}
              isRequired={isRequired}
            />
          </FormLabel>
          <div className={cn('flex flex-col gap-4 pt-2', className)}>
            <SelectableOption
              key={item.id}
              htmlFor={`${name}-${item.id}`}
              label={item.label}
              description={item.description}>
              <FormField
                control={control}
                name={name}
                render={({field}) => {
                  const fieldValue = field.value as boolean;
                  return (
                    <FormItem
                      key={item.id}
                      className="flex flex-row items-start space-x-3 space-y-0">
                      <FormControl>
                        <Checkbox
                          id={`${name}-${item.id}`}
                          dataTestId={`form-checkbox-group-${name}-${item.id}`}
                          checked={fieldValue}
                          onCheckedChange={(checked) => field.onChange(checked)}
                        />
                      </FormControl>
                    </FormItem>
                  );
                }}
              />
            </SelectableOption>
          </div>
          <FormMessage />
        </FormItem>
      )}
    />
  );
};

export default CheckboxField;
