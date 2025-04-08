/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {forwardRef} from 'react';
import Link, {type LinkProps} from 'next/link';

export type NoReferrerLinkProps = Omit<
  React.AnchorHTMLAttributes<HTMLAnchorElement>,
  keyof LinkProps | 'rel'
> &
  LinkProps & {
    children?: React.ReactNode;
  } & React.RefAttributes<HTMLAnchorElement>;

const NoReferrerLink = forwardRef<HTMLAnchorElement, NoReferrerLinkProps>(
  function NoReferrerLink({href, children, ...props}, _) {
    return (
      <Link target="_blank" rel="noopener noreferrer" href={href} {...props}>
        {children}
      </Link>
    );
  },
);

export default NoReferrerLink;
