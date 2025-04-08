/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useRouter} from 'next/navigation';
import {DropdownMenuItem} from '@/components/ui/dropdown-menu';
import {LogOut} from 'lucide-react';

const UserLogoutMenuItem = ({
  logoutUrl,
  withTestId,
}: {
  withTestId?: boolean;
  logoutUrl: string;
}) => {
  const router = useRouter();

  return (
    <DropdownMenuItem
      data-testid={withTestId ? 'logout-btn' : undefined}
      onClick={() => router.push(logoutUrl)}>
      <LogOut className="mr-2 h-4 w-4" />
      <span>Log Out</span>
    </DropdownMenuItem>
  );
};

export default UserLogoutMenuItem;
