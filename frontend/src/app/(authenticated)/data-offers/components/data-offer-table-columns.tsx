/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTableColumnHeader} from '@/components/data-column-header';
import {AssetLink} from '@/components/links/asset-link';
import {type ContractDefinitionEntry} from '@/lib/api/client/generated';
import {type ColumnDef} from '@tanstack/react-table';
import {useTranslations} from 'next-intl';
import DataOfferActionMenu from './data-offer-action-menu';

type ColumnType = ColumnDef<ContractDefinitionEntry>[];

export const useDataOfferTableColumns = (): ColumnType => {
  const t = useTranslations();

  return [
    {
      accessorKey: 'contractDefinitionId',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('Pages.PublishDataOffer.dataOfferId')}
        />
      ),
      cell: ({row}) => {
        return row.original.contractDefinitionId;
      },
    },
    {
      accessorKey: 'accessPolicyId',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('Pages.DataOfferList.accessPolicy')}
        />
      ),
      cell: ({row}) => {
        return row.original.accessPolicyId;
      },
    },
    {
      accessorKey: 'contractPolicyId',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('Pages.DataOfferList.contractPolicy')}
        />
      ),
      cell: ({row}) => {
        return row.original.contractPolicyId;
      },
    },
    {
      id: 'assets',
      accessorFn: (row: ContractDefinitionEntry) =>
        row.assetSelector.map((x) => x.operandRight.value).join(', '),
      header: ({column}) => (
        <DataTableColumnHeader column={column} title={t('General.assets')} />
      ),
      cell: ({row}) => {
        return row.original.assetSelector.map((asset, i) => {
          if (asset.operandRight.value)
            return (
              <AssetLink
                key={`asset-${i}`}
                assetId={asset.operandRight.value}
                assetName={asset.operandRight.value}
              />
            );
        });
      },
    },
    {
      id: 'actions',
      cell: ({row}) => {
        return (
          <DataOfferActionMenu
            contractDefinitionId={row.original.contractDefinitionId}
            dataOffer={row.original}
          />
        );
      },
    },
  ];
};
