/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {ClientsideDataTable} from '@/components/clientside-data-table';
import {buildWordFilter} from '@/lib/utils/build-word-filter';
import {useBusinessPartnerGroupTableColumns} from './business-partner-group-table-columns';
import {useTranslations} from 'next-intl';
import {urls} from '@/lib/urls';
import type {BusinessPartnerGroupListPageEntry} from '@sovity.de/edc-client';

interface BusinessPartnerGroupTableProps {
  data: BusinessPartnerGroupListPageEntry[];
}

const wordFilter = buildWordFilter((row) => {
  return [
    row.getValue('groupId'),
    ...row.getValue<string[]>('businessPartnerNumbers'),
  ];
});

const BusinessPartnerGroupTable = ({data}: BusinessPartnerGroupTableProps) => {
  const t = useTranslations();
  return (
    <ClientsideDataTable
      columns={useBusinessPartnerGroupTableColumns()}
      data={data}
      wordFilter={wordFilter}
      headerButtonLink={urls.businessPartnerGroups.createPage()}
      headerButtonText={t('Pages.BusinessPartnerGroupsCreateForm.title')}
      rowLink={(row) =>
        urls.businessPartnerGroups.editPage(row.original.groupId)
      }
    />
  );
};

export default BusinessPartnerGroupTable;
