/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {forwardRef, type ReactNode} from 'react';
import Link from 'next/link';
import {cn} from '@/lib/utils/css-utils';
import {ExternalLinkIcon} from 'lucide-react';
import {Button, type ButtonProps} from '../ui/button';
import NoReferrerLink from './no-referrer-link';

export type ExternalLinkProps = Omit<ButtonProps, 'asChild' | 'dataTestId'> & {
  children: ReactNode;
  href: string;
  noIcon?: boolean;
  showReferrer?: boolean;
  noNewTab?: boolean;
  whitespaceNormal?: boolean;
  dataTestId?: string;
};

const ExternalLink = forwardRef<HTMLButtonElement, ExternalLinkProps>(
  function ExternalLink(
    {
      href,
      children,
      noIcon,
      showReferrer,
      whitespaceNormal,
      noNewTab,
      ...props
    },
    ref,
  ) {
    const LinkContent = () => (
      <>
        <span className={cn({'whitespace-normal': whitespaceNormal})}>
          {children}
        </span>
        {noIcon ? null : <ExternalLinkIcon className="h-4 w-4" />}
      </>
    );

    return (
      <Button
        ref={ref}
        variant="link"
        size="fit"
        dataTestId={props.dataTestId ?? 'external-link'}
        asChild
        {...props}>
        {showReferrer ? (
          <Link
            href={href}
            className="flex items-center gap-1"
            target={noNewTab ? undefined : '_blank'}>
            <LinkContent />
          </Link>
        ) : (
          <NoReferrerLink
            href={href}
            className="flex items-center gap-1"
            target={noNewTab ? undefined : '_blank'}>
            <LinkContent />
          </NoReferrerLink>
        )}
      </Button>
    );
  },
);

export default ExternalLink;
