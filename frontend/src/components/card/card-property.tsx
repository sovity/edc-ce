/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useEffect, useRef} from 'react';
import {type SvgIcon} from '@/lib/utils/svg-icon';
import {cn} from '@/lib/utils/css-utils';

export interface CardPropertyProps {
  name: string;
  children: React.ReactNode;
  Icon: SvgIcon;
  noLineClamp?: boolean;
  id?: number;
  setIsTruncated?: (id: number, isTruncated: boolean) => void;
}

const CardProperty = ({
  name,
  Icon,
  children,
  noLineClamp,
  id,
  setIsTruncated,
}: CardPropertyProps) => {
  const textRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const checkTruncation = () => {
      if (textRef.current && id !== undefined && setIsTruncated) {
        setIsTruncated(
          id,
          textRef.current.scrollWidth > textRef.current.clientWidth,
        );
      }
    };

    checkTruncation();
    window.addEventListener('resize', checkTruncation);

    return () => {
      window.removeEventListener('resize', checkTruncation);
    };
  }, [id, setIsTruncated]);

  return (
    <div className="space-y-1">
      <dt className="flex items-center gap-1 text-sm text-gray-400">
        <Icon className="h-6 w-5 text-gray-400" aria-hidden="true" />
        <span className="line-clamp-1">{name}</span>
      </dt>
      <dd
        data-testid={`card-property-${name}`}
        ref={textRef}
        className={cn(
          `text-left text-sm leading-6 text-muted-foreground`,
          !noLineClamp && 'overflow-hidden text-ellipsis whitespace-nowrap',
        )}>
        {children}
      </dd>
    </div>
  );
};

export default CardProperty;
