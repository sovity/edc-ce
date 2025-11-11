/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {ClientsideDataTable} from '@/components/clientside-data-table';
import type {VaultSecretListPageEntry} from '@/lib/api/client/generated';
import {buildWordFilter} from '@/lib/utils/build-word-filter';
import {urls} from '@/lib/urls';
import {useTranslations} from 'next-intl';
import {useVaultSecretsTableColumns} from './vault-secrets-table-columns';

interface VaultSecretsTableProps {
  data: VaultSecretListPageEntry[];
}

const wordFilter = buildWordFilter((row) => {
  return [row.getValue('key'), row.getValue('description')];
});

const VaultSecretsTable = ({data}: VaultSecretsTableProps) => {
  const t = useTranslations();

  return (
    <ClientsideDataTable
      columns={useVaultSecretsTableColumns()}
      data={data}
      wordFilter={wordFilter}
      headerButtonLink={urls.vaultSecrets.createPage()}
      headerButtonText={t('Pages.VaultSecretsList.createSecret')}
      rowLink={(row) => urls.vaultSecrets.editPage(row.original.key)}
    />
  );
};

export default VaultSecretsTable;
