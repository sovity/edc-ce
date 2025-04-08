/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTableColumnHeader} from '@/components/data-column-header';
import {type UiAsset} from '@/lib/api/client/generated';
import {type ColumnDef} from '@tanstack/react-table';
import {useTranslations} from 'next-intl';
import AssetHeaderStack from '@/components/stacks/asset-header-stack';
import AssetActionMenu from '@/app/(authenticated)/assets/components/asset-action-menu';

type ColumnType = ColumnDef<UiAsset>[];

export const useAssetTableColumns = (): ColumnType => {
  const t = useTranslations();

  return [
    {
      accessorKey: 'title',
      header: ({column}) => (
        <DataTableColumnHeader column={column} title={t('General.asset')} />
      ),
      cell: ({row}) => {
        return (
          <AssetHeaderStack
            assetId={row.original.assetId}
            assetName={row.original.title}
            dataSourceAvailability={row.original.dataSourceAvailability}
            size={'table-cell'}
          />
        );
      },
    },
    {
      accessorKey: 'descriptionShortText',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('General.description')}
        />
      ),
      cell: ({row}) => {
        return (
          <div className="line-clamp-2">
            {row.original.descriptionShortText}
          </div>
        );
      },
    },
    {
      id: 'actions',
      cell: ({row}) => {
        return <AssetActionMenu asset={row.original} />;
      },
    },
    // invisible columns
    {
      accessorKey: 'dataSourceAvailability',
    },
    {
      accessorKey: 'assetId',
    },
  ];
};
