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
import {Button, type ButtonProps} from '../ui/button';

export type InternalLinkProps = Omit<ButtonProps, 'asChild'> & {
  children: ReactNode;
  href: string;
  alsoExec?: () => void;
};

const InternalLink = forwardRef<HTMLButtonElement, InternalLinkProps>(
  function InternalLink({href, children, dataTestId, alsoExec, ...props}, ref) {
    return (
      <Button ref={ref} dataTestId={dataTestId} asChild {...props}>
        <Link
          href={href}
          onClick={() => alsoExec && alsoExec()}
          data-testid={dataTestId}>
          {children}
        </Link>
      </Button>
    );
  },
);

export default InternalLink;
