/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTable} from '@/components/data-table';
import {AssetListSortProperty} from '@/lib/api/client/generated';
import {urls} from '@/lib/urls';
import {useTranslations} from 'next-intl';
import {useAssetTableColumns} from './asset-table-columns';
import {queryKeys} from '@/lib/queryKeys';
import {api} from '@/lib/api/client';

const AssetTable = () => {
  const t = useTranslations();

  return (
    <DataTable
      columns={useAssetTableColumns()}
      buildDataKey={(params) => queryKeys.assets.listPage(params)}
      getData={({query, page, pageSize, sorting}) => {
        return api.uiApi.assetListPage({
          assetListPageFilter: {
            page,
            pageSize,
            query,
            sort: sorting?.map(({desc, id}) => ({
              columnName: (() => {
                switch (id) {
                  case 'title':
                    return AssetListSortProperty.Title;
                  case 'descriptionShortText':
                    return AssetListSortProperty.DescriptionShortText;
                }
              })()!,
              descending: desc,
            })),
          },
        });
      }}
      headerButtonLink={urls.assets.createPage()}
      headerButtonText={t('Pages.AssetList.createAsset')}
      rowLink={(row) => urls.assets.detailPage(row.original.assetId)}
    />
  );
};

export default AssetTable;
