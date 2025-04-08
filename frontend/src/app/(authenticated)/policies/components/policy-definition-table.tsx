/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTable} from '@/components/data-table';
import {type PolicyDefinitionMapped} from '@/app/(authenticated)/policies/components/policy-definition-mapped';
import {buildWordFilter} from '@/lib/utils/build-word-filter';
import {urls} from '@/lib/urls';
import {useTranslations} from 'next-intl';
import {usePolicyDefinitionTableColumns} from './policy-definition-table-columns';

interface PolicyDefinitionsTableProps {
  data: PolicyDefinitionMapped[];
}

const wordFilter = buildWordFilter((row) => {
  return [row.getValue('policyDefinitionId'), row.getValue('jsonLd')];
});

const invisibleColumns = ['jsonLd'];

const PolicyDefinitionTable = ({data}: PolicyDefinitionsTableProps) => {
  const t = useTranslations();
  return (
    <DataTable
      columns={usePolicyDefinitionTableColumns()}
      data={data}
      invisibleColumns={invisibleColumns}
      wordFilter={wordFilter}
      headerButtonLink={urls.policies.createPage()}
      headerButtonDataTestId={'policy-definition-create-button'}
      headerButtonText={t('Pages.PolicyList.newPolicy')}
    />
  );
};

export default PolicyDefinitionTable;
