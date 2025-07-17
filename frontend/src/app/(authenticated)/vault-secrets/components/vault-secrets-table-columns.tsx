/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTableColumnHeader} from '@/components/data-column-header';
import type {VaultSecretListPageEntry} from '@/lib/api/client/generated';
import {type ColumnDef} from '@tanstack/react-table';
import {useTranslations} from 'next-intl';
import VaultSecretActionMenu from './vault-secret-action-menu';
import LocalTimeAgo from '@/components/local-time-ago';

type ColumnType = ColumnDef<VaultSecretListPageEntry>[];

export const useVaultSecretsTableColumns = (): ColumnType => {
  const t = useTranslations();

  return [
    {
      accessorKey: 'key',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('Pages.VaultSecretsList.vaultKey')}
        />
      ),
    },
    {
      accessorKey: 'description',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('General.description')}
        />
      ),
    },
    {
      accessorKey: 'updatedAt',
      header: ({column}) => (
        <DataTableColumnHeader column={column} title={t('General.updated')} />
      ),
      cell: ({row}) => {
        return <LocalTimeAgo date={row.original.updatedAt} />;
      },
    },
    {
      id: 'actions',
      cell: ({row}) => {
        return <VaultSecretActionMenu vaultSecret={row.original} />;
      },
    },
  ];
};
