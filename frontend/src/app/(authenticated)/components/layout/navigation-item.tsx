/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import Link from 'next/link';
import {usePathname} from 'next/navigation';
import {type SvgIcon} from '@/lib/utils/svg-icon';
import {cn} from '@/lib/utils/css-utils';
import {toKebabCase} from '@/lib/utils/string-utils';

export interface NavigationItemProps {
  name: string;
  href: string;
  Icon: SvgIcon;
  onClick?: () => void;
}

export const normalize = (href: string, pathname: string) => {
  if (href === '/') {
    return {href, pathname};
  }
  return {
    href: href.endsWith('/') ? href.slice(0, -1) : href,
    pathname: pathname.endsWith('/') ? pathname.slice(0, -1) : pathname,
  };
};

const isNavItemHighlighted = (href: string, pathname: string) => {
  const normalized = normalize(href, pathname);
  return normalized.href === normalized.pathname;
};

const NavigationItem = ({name, href, Icon, onClick}: NavigationItemProps) => {
  const pathname = usePathname();
  const dataTestId = toKebabCase(`nav-item-${name}`);

  return (
    <Link
      data-testid={dataTestId}
      href={href}
      onClick={onClick}
      className={cn(
        isNavItemHighlighted(href, pathname)
          ? 'bg-brand-lightblue font-semibold text-brand-darkblue'
          : 'font-medium text-gray-700 hover:bg-brand-lightblue',
        'group flex gap-x-3 rounded-md p-2 text-sm leading-6',
      )}>
      <Icon
        className={cn(
          isNavItemHighlighted(href, pathname)
            ? 'text-brand-darkblue'
            : 'text-gray-400',
          'h-6 w-6 shrink-0',
        )}
        aria-hidden="true"
      />
      {name}
    </Link>
  );
};

export default NavigationItem;
