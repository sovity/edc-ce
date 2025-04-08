/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {Button} from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {useConfig} from '@/lib/hooks/use-config';
import {User2Icon} from 'lucide-react';
import UserLogoutMenuItem from './user-logout-menu-item';

interface DropDownMenuProps {
  withTestId?: boolean;
}

export function UserDropdownMenu({withTestId}: DropDownMenuProps) {
  const config = useConfig();
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          dataTestId={withTestId ? 'user-menu-btn' : 'user-menu-btn-no-test-id'}
          variant="outline"
          size="icon"
          className="h-10 w-10 rounded-full">
          <User2Icon className="size-5 text-gray-700" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="mr-4 w-56">
        {config?.logoutUrl && (
          <UserLogoutMenuItem
            withTestId={withTestId}
            logoutUrl={config?.logoutUrl}
          />
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
