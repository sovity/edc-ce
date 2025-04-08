/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTable} from '@/components/data-table';
import {useCatalogDataOfferTableColumns} from '@/app/(authenticated)/catalog/[participantId]/[endpointUrl]/components/catalog-data-offer-table-columns';
import {buildWordFilter} from '@/lib/utils/build-word-filter';
import {type UiDataOffer} from '@sovity.de/edc-client';
import {urls} from '@/lib/urls';

interface CatalogDataOfferTableProps {
  data: UiDataOffer[];
}

const wordFilter = buildWordFilter((row) => {
  return [
    row.getValue('title'),
    row.getValue('descriptionShortText'),
    row.getValue('dataSourceAvailability'),
  ];
});

const invisibleColumns = ['assetId', 'dataSourceAvailability'];

const CatalogDataOfferTable = ({data}: CatalogDataOfferTableProps) => {
  return (
    <DataTable
      columns={useCatalogDataOfferTableColumns()}
      data={data}
      wordFilter={wordFilter}
      invisibleColumns={invisibleColumns}
      rowLink={(row) =>
        urls.catalog.detailPage(
          row.original.participantId,
          row.original.endpoint,
          row.original.asset.assetId,
        )
      }
    />
  );
};

export default CatalogDataOfferTable;
