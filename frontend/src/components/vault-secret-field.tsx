/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type Control} from 'react-hook-form';
import {AsyncComboboxField} from './form/async-combobox-field';
import {api} from '@/lib/api/client';
import {queryKeys} from '@/lib/queryKeys';
import {CreateVaultSecretForm} from '@/app/(authenticated)/vault-secrets/create/components/create-vault-secret-form';
import {useTranslations} from 'next-intl';

const VaultSecretField = ({
  className,
  control,
  name,
  label,
  isRequired,
}: {
  className?: string;
  control: Control<any>;
  name: string;
  label: string;
  isRequired?: boolean;
}) => {
  const t = useTranslations();
  return (
    <AsyncComboboxField
      className={className}
      control={control}
      name={name}
      label={label}
      isRequired={isRequired}
      loadItems={(query) =>
        api.uiApi
          .listVaultSecretsPage({
            vaultSecretQuery: {
              searchQuery: query,
              limit: 10,
            },
          })
          .then((res) =>
            res.map((item) => ({
              id: item.key,
              label: item.key,
              description: item.description,
            })),
          )
      }
      buildQueryKey={(query) => queryKeys.vaultSecrets.listPage(query, 10)}
      createDescription={t('General.vaultSecretField.createDescription')}
      searchPlaceholder={t('General.vaultSecretField.searchPlaceholder')}
      selectPlaceholder={t('General.vaultSecretField.selectPlaceholder')}
      renderCreateDialog={(query, onSubmit) => (
        <CreateVaultSecretForm
          fromDialog
          defaultKey={query}
          onSubmitExec={({key}) => onSubmit(key)}
        />
      )}
    />
  );
};

export default VaultSecretField;
