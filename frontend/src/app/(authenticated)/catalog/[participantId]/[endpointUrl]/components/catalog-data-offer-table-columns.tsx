/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTableColumnHeader} from '@/components/data-column-header';
import {type UiDataOffer} from '@sovity.de/edc-client';
import {type ColumnDef} from '@tanstack/react-table';
import {useTranslations} from 'next-intl';
import CatalogDataOfferHeaderStack from '@/components/stacks/catalog-data-offer-header-stack';
import CatalogDataOfferActionMenu from '@/app/(authenticated)/catalog/[participantId]/[endpointUrl]/components/catalog-data-offer-action-menu';

type ColumnType = ColumnDef<UiDataOffer>[];

export const useCatalogDataOfferTableColumns = (): ColumnType => {
  const t = useTranslations();

  return [
    {
      accessorKey: 'title',
      accessorFn: (row) => row.asset.title,
      header: ({column}) => (
        <DataTableColumnHeader column={column} title={t('General.dataOffer')} />
      ),
      cell: ({row}) => {
        return (
          <CatalogDataOfferHeaderStack
            participantId={row.original.participantId}
            endpointUrl={decodeURIComponent(row.original.endpoint)}
            assetId={row.original.asset.assetId}
            assetName={row.original.asset.title}
            dataSourceAvailability={row.original.asset.dataSourceAvailability}
            size={'table-cell'}
          />
        );
      },
    },
    {
      accessorKey: 'descriptionShortText',
      accessorFn: (row) => row.asset.descriptionShortText,
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('General.description')}
        />
      ),
      cell: ({row}) => {
        return (
          <div className="line-clamp-2">
            {row.original.asset.descriptionShortText}
          </div>
        );
      },
    },
    {
      id: 'actions',
      cell: ({row}) => <CatalogDataOfferActionMenu dataOffer={row.original} />,
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
