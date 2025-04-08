/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import BaseBadge from '@/components/badges/base-badge';
import {DataTableColumnHeader} from '@/components/data-column-header';
import LocalTimeAgo from '@/components/local-time-ago';
import {type ContractAgreementTransferProcess} from '@/lib/api/client/generated';
import {sortByTimeAgo} from '@/lib/utils/sort-utils';
import {type ColumnDef} from '@tanstack/react-table';
import {useTranslations} from 'next-intl';

type ColumnType = ColumnDef<ContractAgreementTransferProcess>[];

export const useContractTransferTableColumns = (): ColumnType => {
  const t = useTranslations();

  return [
    {
      accessorKey: 'transferProcessId',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('General.transferId')}
        />
      ),
    },
    {
      accessorKey: 'lastUpdatedDate',
      sortingFn: sortByTimeAgo((row) => row.lastUpdatedDate),
      header: ({column}) => (
        <DataTableColumnHeader column={column} title={t('General.updated')} />
      ),
      cell: ({row}) => {
        return <LocalTimeAgo date={row.original.lastUpdatedDate} />;
      },
    },
    {
      id: 'state',
      accessorFn: (row) => row.state.name,
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('Pages.TransferHistory.state')}
        />
      ),
      cell: ({row}) => {
        return (
          <BaseBadge
            label={row.original.state.name}
            classes={'bg-gray-50 text-gray-700 ring-gray-600/20'}
          />
        );
      },
    },
    {
      id: 'errorMessage',
      header: ({column}) => (
        <DataTableColumnHeader column={column} title={t('General.error')} />
      ),
      cell: ({row}) => {
        return (
          <div className="line-clamp-2 text-xs">
            {row.original.errorMessage}
          </div>
        );
      },
    },
  ];
};
