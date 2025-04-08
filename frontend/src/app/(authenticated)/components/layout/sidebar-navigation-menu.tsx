/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type NavItemGroup, useNavItems} from '@/lib/nav-items';
import NavigationItem from './navigation-item';

interface SidebarNavigationMenuProps {
  onClick?: () => void;
}

const SidebarNavigationMenu = ({}: SidebarNavigationMenuProps) => {
  const navigation = useNavItems();

  return (
    <nav className="flex flex-1 flex-col">
      <ul role="list" className="flex flex-1 flex-col gap-y-7">
        {navigation.map((group) => (
          <SidebarNavigationMenuGroup key={group.title} group={group} />
        ))}
      </ul>
    </nav>
  );
};

const SidebarNavigationMenuGroup = ({group}: {group: NavItemGroup}) => (
  <li>
    {group.title && (
      <div className="text-xs font-semibold leading-6 text-gray-400">
        {group.title}
      </div>
    )}
    <ul role="list" className="-mx-2 mt-2 space-y-1">
      {group.items.map((item) => (
        <li key={item.name}>
          <NavigationItem name={item.name} href={item.href} Icon={item.icon} />
        </li>
      ))}
    </ul>
  </li>
);

export default SidebarNavigationMenu;
