/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTable} from '@/components/data-table';
import {urls} from '@/lib/urls';
import {useTranslations} from 'next-intl';
import {useAssetTableColumns} from './asset-table-columns';
import {queryKeys} from '@/lib/queryKeys';
import {api} from '@/lib/api/client';
import {AssetsPageSortProperty, SortByDirection} from '@sovity.de/edc-client';

const AssetTable = () => {
  const t = useTranslations();

  return (
    <DataTable
      columns={useAssetTableColumns()}
      buildDataKey={(params) => queryKeys.assets.assetsPage(params)}
      getData={async ({searchText, pageOneBased, pageSize, sorting}) => {
        const {assets, pagination} = await api.uiApi.assetsPage({
          assetsPageRequest: {
            pagination: {pageOneBased, pageSize},
            searchText,
            sortBy: sorting?.map(({desc, id}) => ({
              field: (() => {
                switch (id) {
                  case 'title':
                    return AssetsPageSortProperty.Title;
                  case 'descriptionShortText':
                    return AssetsPageSortProperty.Description;
                }
              })()!,
              direction: desc
                ? SortByDirection.Descending
                : SortByDirection.Ascending,
            })),
          },
        });
        return {content: assets, pagination};
      }}
      headerButtonLink={urls.assets.createPage()}
      headerButtonText={t('Pages.AssetList.createAsset')}
      rowLink={(row) => urls.assets.detailPage(row.original.assetId)}
    />
  );
};

export default AssetTable;
