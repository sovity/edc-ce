/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useTranslations} from 'next-intl';
import {type UseFormReturn} from 'react-hook-form';
import {z} from 'zod';
import {useCallback, useEffect, useState} from 'react';
import InputField from '@/components/form/input-field';
import SelectField from '@/components/form/select-field';
import {api} from '@/lib/api/client';
import {useQuery} from '@tanstack/react-query';
import VaultSecretField from '@/components/vault-secret-field';
import {Button} from '@/components/ui/button';

export const azureDataSourceSchema = z.object({
  accountKey: z.string().min(1, 'Account Key is required'),
  storageAccountName: z.string().min(1, 'Shared Access Secret is required'),
  containerName: z.string().min(1, 'Container is required'),
  blobName: z.string().min(1, 'Blob is required'),
});

export type AzureDataSourceFormValue = z.infer<typeof azureDataSourceSchema>;

export const AzureDataSourceForm = ({
  form,
  formKey,
}: {
  form: UseFormReturn<any>;
  formKey?: string;
}) => {
  const t = useTranslations();

  const value = (
    formKey ? form.watch(formKey) : form.watch()
  ) as AzureDataSourceFormValue;

  const fieldKey = useCallback(
    (key: string): string => (formKey ? `${formKey}.${key}` : key),
    [formKey],
  );

  const [storageAccountName, setStorageAccountName] = useState<string | null>(
    null,
  );

  useEffect(() => {
    setStorageAccountName(null);
    form.setValue(fieldKey('containerName'), '');
    form.setValue(fieldKey('blobName'), '');
  }, [
    value.storageAccountName,
    value.accountKey,
    form,
    setStorageAccountName,
    fieldKey,
  ]);

  useEffect(() => {
    form.setValue(fieldKey('blobName'), '', {shouldValidate: true});
  }, [value.containerName, form, fieldKey]);

  const searchContainers = async () => {
    setStorageAccountName(value.storageAccountName);
    void form.trigger(fieldKey('containerName'));
  };

  const {data: containers, isLoading: containersLoading} = useQuery(
    ['azure-containers', storageAccountName, value.accountKey],
    async () =>
      storageAccountName && value.accountKey
        ? api.uiApi.listAzureStorageContainers({
            azureStorageListContainersRequest: {
              storageAccountName: storageAccountName,
              storageAccountVaultKey: value.accountKey,
            },
          })
        : null,
  );

  const {data: blobs, isLoading: blobsLoading} = useQuery(
    ['azure-blobs', storageAccountName, value.containerName, value.accountKey],
    async () =>
      storageAccountName && value.containerName && value.accountKey
        ? api.uiApi.listAzureStorageBlobs({
            azureStorageListBlobsRequest: {
              storageAccountName: storageAccountName,
              containerName: value.containerName,
              storageAccountVaultKey: value.accountKey,
            },
          })
        : null,
  );

  return (
    <>
      <div className="flex flex-wrap items-end gap-4">
        <InputField
          className="flex-1"
          isRequired
          control={form.control}
          name={fieldKey('storageAccountName')}
          placeholder="my-account"
          label={t('Pages.DataOfferCreate.storageAccountName')}
        />
        <VaultSecretField
          isRequired
          className="flex-1"
          name={fieldKey('accountKey')}
          label={t('Pages.DataOfferCreate.sharedAccessSecret')}
          control={form.control}
        />
        <Button
          className="justify-center align-bottom"
          disabled={
            !value.storageAccountName?.trim()?.length ||
            !value.accountKey?.trim()?.length
          }
          dataTestId="search-containers"
          onClick={(e) => {
            e.preventDefault();
            void searchContainers();
          }}>
          {t('Pages.DataOfferCreate.listContainers')}
        </Button>
      </div>
      {(containers || containersLoading) && (
        <SelectField
          isRequired
          control={form.control}
          isLoading={containersLoading}
          name={fieldKey('containerName')}
          label={t('Pages.DataOfferCreate.containerName')}
          items={
            containers?.map((container) => ({
              id: container,
              label: container,
            })) ?? []
          }
          placeholder="my-container-1"
        />
      )}
      {(blobs || blobsLoading) && (
        <SelectField
          isRequired
          isLoading={blobsLoading}
          control={form.control}
          name={fieldKey('blobName')}
          label={t('Pages.DataOfferCreate.blobName')}
          items={
            blobs?.map((blob) => ({
              id: blob,
              label: blob,
            })) ?? []
          }
          placeholder="my-blob-1"
        />
      )}
    </>
  );
};
