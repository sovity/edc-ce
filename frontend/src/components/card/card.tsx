/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import React, {useCallback, useState} from 'react';
import {type SvgIcon} from '@/lib/utils/svg-icon';
import {cn} from '@/lib/utils/css-utils';
import InternalLink from '../links/internal-link';
import {Button} from '../ui/button';
import CardProperty from './card-property';
import useShowAllPropertiesDialog from './use-show-all-properties-dialog';

export interface CardPropertyType {
  name: string;
  icon: SvgIcon;
  value: React.ReactNode;
}

export interface CardProps {
  id: string;
  title: string;
  titleUrl?: string;
  subtitle: string;
  properties: CardPropertyType[];
  topRightContent?: React.ReactNode;
  bottomLeftContent?: React.ReactNode;
  bottomRightContent?: React.ReactNode;
  className?: string;
}

const Card = (data: CardProps) => {
  const showAllPropertiesDialog = useShowAllPropertiesDialog(
    data.id,
    data.title,
    data.subtitle,
    data.properties,
  );
  const numVisibleProperties = 3;
  const visibleProperties = data.properties.slice(0, numVisibleProperties);
  const numNotVisibleProperties = data.properties.length - numVisibleProperties;

  const [isTruncated, setIsTruncated] = useState<Record<number, boolean>>({});

  const showViewAllButton =
    numNotVisibleProperties > 0 || Object.values(isTruncated).some(Boolean);

  const setIsTruncatedForProperty = useCallback(
    (id: number, isTruncated: boolean) => {
      setIsTruncated((prev) => ({...prev, [id]: isTruncated}));
    },
    [setIsTruncated],
  );

  return (
    <div
      className={cn(
        'flex rounded-md border border-border shadow-sm',
        data.className && data.className,
      )}>
      <div className="flex grow flex-col">
        <div className="flex justify-between px-5 pt-4">
          <div>
            {data.titleUrl ? (
              <InternalLink
                dataTestId={`card-title-${data.id}`}
                href={data.titleUrl}
                variant="link"
                size="fit"
                className="pr-5 text-base font-semibold text-brand-darkblue">
                {data.title}
              </InternalLink>
            ) : (
              <h3 className="pr-5 text-base font-semibold text-brand-darkblue">
                {data.title}
              </h3>
            )}
            <div className="text-sm text-muted-foreground">{data.subtitle}</div>
          </div>
          <div className="mt-1">
            <div className="flex items-center gap-3">
              {data.topRightContent ? data.topRightContent : null}
            </div>
          </div>
        </div>
        <div className="grow">
          <div className="mt-4 grid grid-cols-3 gap-3 px-5">
            {visibleProperties.map((property, propertyIndex) => (
              <CardProperty
                key={`${data.id}-property-${property.name}`}
                id={propertyIndex}
                setIsTruncated={setIsTruncatedForProperty}
                name={property.name}
                Icon={property.icon}>
                {property.value}
              </CardProperty>
            ))}
          </div>
          <div className="flex justify-center">
            {showViewAllButton ? (
              <Button
                dataTestId={`btn-view-all-${data.id}`}
                onClick={() => showAllPropertiesDialog()}
                variant={'link'}
                size="fit"
                className="mt-3">
                View All
                {numNotVisibleProperties > 0
                  ? ` (+${numNotVisibleProperties} more)`
                  : ''}
              </Button>
            ) : null}
          </div>
        </div>
        {data.bottomLeftContent || data.bottomRightContent ? (
          <div className="mt-4 flex items-center justify-between border-t border-border px-5 pb-4 pt-3">
            <div className="flex flex-wrap items-center gap-2">
              {data.bottomLeftContent ? data.bottomLeftContent : null}
            </div>

            <div className="flex items-center gap-4">
              {data.bottomRightContent ? data.bottomRightContent : null}
            </div>
          </div>
        ) : (
          <div className="mt-7"></div>
        )}
      </div>
    </div>
  );
};

export default Card;
