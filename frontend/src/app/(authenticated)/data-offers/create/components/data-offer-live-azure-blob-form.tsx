/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import TextareaField from '@/components/form/textarea-field';
import {type DataOfferLiveType} from '@/app/(authenticated)/data-offers/create/components/data-offer-type-schema';
import {jsonString} from '@/lib/utils/zod/schema-utils';
import {useTranslations} from 'next-intl';
import {type UseFormReturn} from 'react-hook-form';
import {Button} from '@/components/ui/button';
import {Dialog, DialogContent} from '@/components/ui/dialog';
import {z} from 'zod';
import {useEffect, useState} from 'react';
import AzureBlobSelector from '@/components/azure-blob-selector/azure-blob-selector';

export const dataOfferLiveAzureBlobSchema = z.object({
  offerLiveType: z.literal('AZURE_BLOB' satisfies DataOfferLiveType),
  dataAddressJson: jsonString(),
});

export type DataOfferLiveAzureBlobFormValue = z.infer<
  typeof dataOfferLiveAzureBlobSchema
>;

export const DataOfferLiveAzureBlobForm = ({
  form,
  formKeyDataOfferTypeLive,
}: {
  form: UseFormReturn<any>;
  formKeyDataOfferTypeLive: string;
}) => {
  const t = useTranslations();

  const [isAzureOpen, setIsAzureOpen] = useState(false);
  const [selectionAllowed, setSelectionAllowed] = useState(false);
  const [azureDataAddress, setAzureDataAddress] = useState('');

  const fieldKey = (key: string): string =>
    formKeyDataOfferTypeLive === ''
      ? formKeyDataOfferTypeLive
      : `${formKeyDataOfferTypeLive}.${key}`;

  const value = form.watch(
    formKeyDataOfferTypeLive,
  ) as DataOfferLiveAzureBlobFormValue;

  useEffect(() => {
    if (azureDataAddress) {
      form.setValue(fieldKey('dataAddressAzure'), azureDataAddress);
    }
  }, [azureDataAddress]);

  return (
    value.offerLiveType === 'AZURE_BLOB' && (
      <>
        <TextareaField
          control={form.control}
          name={fieldKey('dataAddressAzure')}
          placeholder='"dataAddress": { "type": ... }'
          label={t('Pages.DataOfferCreate.dataSourceTypeAzureBlob')}
        />
        <Button
          dataTestId={'btn-open-azure-blob-select'}
          type="button"
          onClick={() => setIsAzureOpen(true)}>
          Select Blob
        </Button>

        <Dialog open={isAzureOpen} onOpenChange={setIsAzureOpen}>
          <DialogContent className="w-auto">
            <AzureBlobSelector
              setSelectionAllowed={setSelectionAllowed}
              setAzureDataAddress={setAzureDataAddress}
              selectionAllowed={selectionAllowed}
              setIsOpen={setIsAzureOpen}
            />
          </DialogContent>
        </Dialog>
      </>
    )
  );
};
