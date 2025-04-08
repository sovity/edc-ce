/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTableColumnHeader} from '@/components/data-column-header';
import LocalTimeAgo from '@/components/local-time-ago';
import {type TransferHistoryEntry} from '@/lib/api/client/generated';
import {sortByTimeAgo} from '@/lib/utils/sort-utils';
import {type ColumnDef} from '@tanstack/react-table';
import {useTranslations} from 'next-intl';
import BaseBadge from '../../../../components/badges/base-badge';
import ContractAgreementHeaderStack from '@/components/stacks/contract-agreement-header-stack';

type ColumnType = ColumnDef<TransferHistoryEntry>[];

export const useTransferHistoryTableColumns = (): ColumnType => {
  const t = useTranslations();

  return [
    {
      accessorKey: 'assetName',
      header: ({column}) => (
        <DataTableColumnHeader column={column} title={t('General.contract')} />
      ),
      cell: ({row}) => {
        return (
          <ContractAgreementHeaderStack
            size={'table-cell'}
            direction={row.original.direction}
            contractAgreementId={row.original.contractAgreementId}
            counterpartyParticipantId={row.original.counterPartyParticipantId}
            assetName={row.original.assetName}
            terminationStatus={'ONGOING'}
          />
        );
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
      accessorKey: 'lastUpdatedDate',
      sortingFn: sortByTimeAgo((row) => row.lastUpdatedDate),
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('Pages.TransferHistory.lastUpdated')}
        />
      ),
      cell: ({row}) => {
        return <LocalTimeAgo date={row.original.lastUpdatedDate} />;
      },
    },
    {
      id: 'transferProcessId',
      accessorFn: (row) => row.transferProcessId,
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('General.transferProcessId')}
        />
      ),
      cell: ({row}) => row.original.transferProcessId,
    },
    // invisible columns
    {
      accessorKey: 'assetId',
    },
    {
      accessorKey: 'direction',
      accessorFn: (row) => row.direction,
    },
    {
      accessorKey: 'counterPartyParticipantId',
      accessorFn: (row) => row.counterPartyParticipantId,
    },
    {
      accessorKey: 'counterPartyConnectorEndpoint',
    },
    {
      accessorKey: 'errorMessage',
    },
  ];
};
