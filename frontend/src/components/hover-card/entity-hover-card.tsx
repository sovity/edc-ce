/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from '@/components/ui/hover-card';
import InternalLink from '../links/internal-link';
import {Button} from '../ui/button';
import EntityHoverCardContent, {
  type EntityHoverCardContentProps,
} from './entity-hover-card-content';

export type EntityHoverCardProps = {
  url?: string;
  children?: React.ReactNode;
  dataTestId?: string;
} & EntityHoverCardContentProps;

const EntityHoverCard = (props: EntityHoverCardProps) => {
  return (
    <HoverCard openDelay={100} closeDelay={100}>
      <HoverCardTrigger asChild>
        {props.url ? (
          <InternalLink
            dataTestId={props.dataTestId ?? 'hover-card-trigger'}
            href={props.url}
            variant="link"
            size="fit">
            {props.children ?? props.title}
          </InternalLink>
        ) : (
          <Button
            dataTestId={props.dataTestId ?? 'hover-card-trigger'}
            variant="fakeLink"
            size="fit">
            {props.children ?? props.title}
          </Button>
        )}
      </HoverCardTrigger>
      <HoverCardContent className="w-[22rem]">
        <EntityHoverCardContent
          entityName={props.entityName}
          title={props.title}
          fields={props.fields}
        />
      </HoverCardContent>
    </HoverCard>
  );
};

export default EntityHoverCard;
