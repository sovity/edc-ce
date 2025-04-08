/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {Badge} from '../ui/badge';
import EntityHoverCardDetail, {
  type HoverCardDetailProps,
} from './entity-hover-card-detail';

export interface EntityHoverCardContentProps {
  title: string;
  entityName: string;
  fields: HoverCardDetailProps[];
}

const EntityHoverCardContent = (props: EntityHoverCardContentProps) => {
  return (
    <>
      <div className="flex items-center justify-between gap-4">
        <div className="line-clamp-2 break-words font-medium">
          {props.title}
        </div>
        <Badge variant="outline" className="h-min whitespace-nowrap">
          {props.entityName}
        </Badge>
      </div>
      <div className="mt-2 grid grid-cols-5 place-content-start gap-x-5 gap-y-1">
        {props.fields
          .filter((it) => it.value != null)
          .map((field, idx) => (
            <EntityHoverCardDetail
              key={idx}
              Icon={field.Icon}
              label={field.label}
              value={field.value}
            />
          ))}
      </div>
    </>
  );
};

export default EntityHoverCardContent;
