/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {cn} from '@/lib/utils/css-utils';

export interface SelectableOptionProps {
  htmlFor: string;
  label: string;
  description?: string | React.ReactNode;
  children: React.ReactNode;
  className?: string;
  labelClassName?: string;
  disabled?: boolean;
}

const SelectableOption = (props: SelectableOptionProps) => {
  return (
    <div className={cn('flex space-x-2', props.className)}>
      {props.children}
      <div className="flex flex-col">
        <label
          htmlFor={props.htmlFor}
          className={cn(
            'text-sm font-medium leading-none',
            props.labelClassName,
            props.disabled && 'cursor-not-allowed opacity-50',
          )}>
          {props.label}
        </label>
        {props.description ? (
          <div
            className={cn(
              'mt-0.5 text-sm text-muted-foreground',
              props.disabled && 'opacity-50',
            )}>
            {props.description}
          </div>
        ) : null}
      </div>
    </div>
  );
};

export default SelectableOption;
