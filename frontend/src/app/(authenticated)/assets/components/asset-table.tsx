/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTable} from '@/components/data-table';
import type {UiAsset} from '@/lib/api/client/generated';
import {buildWordFilter} from '@/lib/utils/build-word-filter';
import {urls} from '@/lib/urls';
import {useTranslations} from 'next-intl';
import {useAssetTableColumns} from './asset-table-columns';

interface AssetTableProps {
  data: UiAsset[];
}

const wordFilter = buildWordFilter((row) => {
  return [
    row.getValue('title'),
    row.getValue('assetId'),
    row.getValue('descriptionShortText'),
    row.getValue('dataSourceAvailability'),
  ];
});

const invisibleColumns = ['assetId', 'dataSourceAvailability'];

const AssetTable = ({data}: AssetTableProps) => {
  const t = useTranslations();

  return (
    <DataTable
      columns={useAssetTableColumns()}
      data={data}
      wordFilter={wordFilter}
      invisibleColumns={invisibleColumns}
      headerButtonLink={urls.assets.createPage()}
      headerButtonText={t('Pages.AssetList.createAsset')}
      rowLink={(row) => urls.assets.detailPage(row.original.assetId)}
    />
  );
};

export default AssetTable;
