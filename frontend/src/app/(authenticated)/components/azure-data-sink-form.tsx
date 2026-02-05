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
import {useEffect, useState} from 'react';
import InputField from '@/components/form/input-field';
import {api} from '@/lib/api/client';
import {useQuery} from '@tanstack/react-query';
import {Button} from '@/components/ui/button';
import ComboboxField from '@/components/form/combobox-field';
import CheckboxField from '@/components/form/checkbox-field';
import ActionConfirmDialog from '@/components/action-confirm-dialog';

export const azureDataSinkSchema = z.object({
  storageAccountName: z.string().min(1, 'Storage Account Name is required'),
  containerName: z.string().min(1, 'Container Name is required'),
  useFolder: z.boolean().optional(),
  folderName: z.string().optional(),
  blobName: z.string().optional(),
});

export type AzureDataSinkFormValue = z.infer<typeof azureDataSinkSchema>;

export const AzureDataSinkForm = ({form}: {form: UseFormReturn<any>}) => {
  const t = useTranslations();

  const value = form.watch() as AzureDataSinkFormValue;

  const [storageAccountName, setStorageAccountName] = useState<string | null>(
    null,
  );

  const searchContainers = async () => {
    setStorageAccountName(value.storageAccountName);
    void form.trigger('containerName');
  };

  const {data: containers} = useQuery(
    ['azure-containers', storageAccountName],
    async () =>
      storageAccountName
        ? api.uiApi.listAzureStorageContainers({
            azureStorageListContainersRequest: {
              storageAccountName: storageAccountName,
              storageAccountVaultKey: storageAccountName + '-key1',
            },
          })
        : null,
  );

  return (
    <>
      <div className="flex flex-wrap items-center gap-4">
        <InputField
          className="flex-1"
          isRequired
          control={form.control}
          name="storageAccountName"
          placeholder="my-account"
          label={t('Pages.DataOfferCreate.storageAccountName')}
          onChangeExec={() => {
            setStorageAccountName(null);
            form.setValue('containerName', '');
            form.setValue('folderName', '');
            form.setValue('useFolder', false);
            form.setValue('blobName', '');
          }}
          description={
            value.storageAccountName &&
            t('Pages.InitiateTransfer.vaultSecretDescription', {
              vaultKey: `${value.storageAccountName}-key1`,
            } as unknown as undefined)
          }
        />
        <Button
          className="justify-center"
          disabled={!value.storageAccountName?.trim()?.length}
          dataTestId="search-containers"
          onClick={(e) => {
            e.preventDefault();
            void searchContainers();
          }}>
          {t('Pages.DataOfferCreate.listContainers')}
        </Button>
      </div>
      {containers && (
        <ComboboxField
          isRequired
          control={form.control}
          name="containerName"
          label={t('Pages.DataOfferCreate.containerName')}
          itemGroups={[
            {
              items:
                containers?.map((container) => ({
                  id: container,
                  label: container,
                })) ?? [],
              heading: 'Containers',
            },
          ]}
          onChangeExec={() => {
            void form.trigger('containerName');
          }}
          selectPlaceholder="my-container-1"
          searchPlaceholder={'Search containers'}
          searchEmptyMessage={t('Pages.InitiateTransfer.noContainersFound')}
          createDescription={t('Pages.InitiateTransfer.createNewContainer')}
          renderCreateDialog={(id, onSubmit) => (
            <ActionConfirmDialog
              label={t('Pages.InitiateTransfer.newContainerConfirmHeader')}
              description={t(
                'Pages.InitiateTransfer.newContainerConfirmDescription',
              )}
              buttonType={'DEFAULT'}
              buttonLabel={t('General.create')}
              onConfirm={() => {
                containers?.push(id);
                onSubmit(id);
              }}
              dismiss={() => null}
            />
          )}
        />
      )}
      {value.containerName && (
        <>
          <CheckboxField
            control={form.control}
            name={'useFolder'}
            label={t('Pages.InitiateTransfer.useFolder')}
            item={{
              id: 'useFolder',
              label: t('Pages.InitiateTransfer.useFolder'),
              description: t('Pages.InitiateTransfer.useFolderDescription'),
            }}
          />
          {value.useFolder && (
            <InputField
              isRequired
              control={form.control}
              name="folderName"
              label={t('Pages.InitiateTransfer.folderName')}
            />
          )}
          <InputField
            isRequired
            control={form.control}
            name="blobName"
            label={t('Pages.DataOfferCreate.blobName')}
            placeholder="my-blob-1"
          />
        </>
      )}
    </>
  );
};
