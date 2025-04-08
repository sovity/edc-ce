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
import {type ContractAgreementCard} from '@/lib/api/client/generated';
import {type ColumnDef} from '@tanstack/react-table';
import {useTranslations} from 'next-intl';
import ContractActionMenu from './contract-action-menu';
import ContractAgreementHeaderStack from '@/components/stacks/contract-agreement-header-stack';

type ColumnType = ColumnDef<ContractAgreementCard>[];

export const useContractTableColumns = (): ColumnType => {
  const t = useTranslations();

  return [
    {
      id: 'assetId',
      accessorFn: (row: ContractAgreementCard) => row.asset.assetId,
      header: ({column}) => (
        <DataTableColumnHeader column={column} title={t('General.contract')} />
      ),
      cell: ({row}) => (
        <ContractAgreementHeaderStack
          size={'table-cell'}
          contractAgreementId={row.original.contractAgreementId}
          counterpartyParticipantId={row.original.counterPartyId}
          terminationStatus={row.original.terminationStatus}
          direction={row.original.direction}
          assetName={row.original.asset.title}
        />
      ),
    },
    {
      accessorKey: 'contractSigningDate',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('Pages.ContractList.signedAt')}
        />
      ),
      cell: ({row}) => (
        <>
          Signed <LocalTimeAgo date={row.original.contractSigningDate} />
        </>
      ),
    },
    {
      accessorKey: 'terminatedAt',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('Pages.ContractList.terminatedAt')}
        />
      ),
      cell: ({row}) =>
        row.original.terminationStatus === 'TERMINATED' ? (
          <>
            Terminated <LocalTimeAgo date={row.original.contractSigningDate} />
          </>
        ) : (
          'Ongoing'
        ),
    },
    {
      id: 'transfers',
      accessorFn: (row: ContractAgreementCard) => row.transferProcesses.length,
      header: ({column}) => (
        <DataTableColumnHeader column={column} title={t('General.transfers')} />
      ),
      cell: ({row}) => (
        <span className="flex justify-center">
          {row.original.transferProcesses.length}
        </span>
      ),
    },
    {
      id: 'actions',
      cell: ({row}) => <ContractActionMenu contractAgreement={row.original} />,
    },
    // Invisible columns
    {
      accessorKey: 'contractAgreementId',
    },
    {
      id: 'creatorOrganizationName',
      accessorFn: (row: ContractAgreementCard) =>
        row.asset.creatorOrganizationName,
    },
  ];
};
