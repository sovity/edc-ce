/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {Fragment} from 'react';
import {cn} from '@/lib/utils/css-utils';
import {type EntityField} from './model/entity-field';

export interface EntityFieldGridProps {
  title?: string;
  fields: EntityField[];
  class?: string;
}

const EntityFieldGrid = (props: EntityFieldGridProps) => {
  return (
    <div className={cn(props.class)}>
      {props.title ? <span className="font-medium">{props.title}</span> : null}
      <div
        className={cn(
          'ml-2 mt-2 grid grid-cols-12 gap-x-6 gap-y-2',
          props.title && 'mt-2',
        )}>
        {props.fields
          .filter((field) => field.value != null)
          .map((field) => (
            <Fragment key={`${props.title}-field-${field.label}`}>
              <div className="col-span-4 whitespace-nowrap text-sm text-muted-foreground">
                {field.label}
              </div>
              <div className="col-span-8 text-sm font-medium">
                {field.value}
              </div>
            </Fragment>
          ))}
      </div>
    </div>
  );
};

export default EntityFieldGrid;
