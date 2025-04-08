/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTable} from '@/components/data-table';
import type {ContractDefinitionEntry} from '@/lib/api/client/generated';
import {buildWordFilter} from '@/lib/utils/build-word-filter';
import {urls} from '@/lib/urls';
import {useTranslations} from 'next-intl';
import {useDataOfferTableColumns} from './data-offer-table-columns';

interface DataOfferTableProps {
  data: ContractDefinitionEntry[];
}

const wordFilter = buildWordFilter((row) => {
  return [
    row.getValue('contractDefinitionId'),
    row.getValue('accessPolicyId'),
    row.getValue('contractPolicyId'),
    row.getValue('assets'),
  ];
});

const DataOfferTable = ({data}: DataOfferTableProps) => {
  const t = useTranslations();

  return (
    <DataTable
      columns={useDataOfferTableColumns()}
      data={data}
      wordFilter={wordFilter}
      headerButtonText={t('Pages.DataOfferList.publishDataOffer')}
      headerButtonLink={urls.dataOffers.publishPage()}
    />
  );
};

export default DataOfferTable;
