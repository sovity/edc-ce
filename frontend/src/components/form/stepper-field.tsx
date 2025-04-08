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
import {Minus, Plus} from 'lucide-react';
import {type Control} from 'react-hook-form';
import {Input} from '../ui/input';

export interface TextFieldProps {
  control: Control<any>;
  name: string;
  label: string;
}

const StepperField = ({control, name, label}: TextFieldProps) => {
  return (
    <FormField
      control={control}
      name={name}
      render={({field}) => {
        return (
          <FormItem>
            <FormLabel className="text-gray-800" htmlFor={name}>
              {label}
            </FormLabel>
            <FormControl>
              <div className="relative flex items-center justify-center space-x-2">
                <Button
                  dataTestId={`btn-decrement-${name}`}
                  type="button"
                  variant="outline"
                  size="icon"
                  className="h-8 w-8 shrink-0 rounded-full"
                  disabled={field.value <= 0}
                  onClick={() =>
                    field.onChange(String(Number(field.value) - 1))
                  }>
                  <Minus />
                </Button>
                <Input
                  id={name}
                  className="border-none text-center text-xl"
                  autoComplete="off"
                  {...field}
                />
                <Button
                  dataTestId={`btn-increment-${name}`}
                  type="button"
                  variant="outline"
                  size="icon"
                  className="h-8 w-8 shrink-0 rounded-full"
                  onClick={() =>
                    field.onChange(String(Number(field.value) + 1))
                  }>
                  <Plus />
                </Button>
              </div>
            </FormControl>
            <FormMessage />
          </FormItem>
        );
      }}
    />
  );
};

export default StepperField;
