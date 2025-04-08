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
import {PlusCircle, Trash} from 'lucide-react';
import {type Control, useFieldArray} from 'react-hook-form';
import FormLabelContent from './form-label-content';

export interface KeyValuePairsFieldProps {
  control: Control<any, any>;
  name: string;
  label: string;
  tooltip?: string;
  keyPlaceholder?: string;
  valuePlaceholder?: string;
}

const KeyValuePairsField = ({
  control,
  name,
  label,
  tooltip,
  keyPlaceholder,
  valuePlaceholder,
}: KeyValuePairsFieldProps) => {
  const {fields, append, remove} = useFieldArray({
    control,
    name,
  });

  return (
    <FormField
      control={control}
      name={name}
      render={() => (
        <FormItem>
          <FormLabel>
            <FormLabelContent label={label} tooltip={tooltip} />
          </FormLabel>
          <div className="flex flex-col gap-4">
            {fields.map((field, index) => (
              <div key={field.id} className="flex gap-4">
                <FormField
                  control={control}
                  name={`${name}.${index}.key`}
                  render={({field}) => (
                    <div className="w-full">
                      <FormControl>
                        <Input
                          data-testid={`form-key-value-pairs-${name}-${index}-key`}
                          autoComplete={'off'}
                          placeholder={keyPlaceholder ?? 'Key'}
                          {...field}
                        />
                      </FormControl>
                      <FormMessage className="mt-2" />
                    </div>
                  )}
                />
                <FormField
                  control={control}
                  name={`${name}.${index}.value`}
                  render={({field}) => (
                    <div className="w-full">
                      <FormControl>
                        <Input
                          dataTestId={`form-key-value-pairs-${name}-${index}-value`}
                          autoComplete={'off'}
                          placeholder={valuePlaceholder ?? 'Value'}
                          {...field}
                        />
                      </FormControl>
                      <FormMessage className="mt-2" />
                    </div>
                  )}
                />
                <Button
                  dataTestId={`form-key-value-pairs-${name}-${index}-remove`}
                  type="button"
                  variant="outline"
                  onClick={() => remove(index)}>
                  <Trash size={16} />
                </Button>
              </div>
            ))}
            <Button
              dataTestId={`form-key-value-pairs-${name}-add`}
              type="button"
              variant="outline"
              onClick={() => append({key: '', value: ''})}
              className="mb-1 flex items-center gap-2">
              <PlusCircle size={16} />
              <span>Add Pair</span>
            </Button>
          </div>
        </FormItem>
      )}
    />
  );
};

export default KeyValuePairsField;
