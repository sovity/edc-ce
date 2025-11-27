/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type DataOfferLiveType} from '@/app/(authenticated)/data-offers/create/components/data-offer-type-schema';
import {type UseFormReturn} from 'react-hook-form';
import {z} from 'zod';
import {
  AzureStorageForm,
  azureStorageSchema,
} from '@/app/(authenticated)/components/azure-storage-form';

export const dataOfferLiveAzureStorageSchema = azureStorageSchema.extend({
  offerLiveType: z.literal('AZURE_STORAGE' satisfies DataOfferLiveType),
});

export type DataOfferLiveAzureStorageFormValue = z.infer<
  typeof dataOfferLiveAzureStorageSchema
>;

export const DataOfferLiveAzureStorageForm = ({
  form,
  formKeyDataOfferTypeLive,
}: {
  form: UseFormReturn<any>;
  formKeyDataOfferTypeLive: string;
}) => {
  const value = form.watch(
    formKeyDataOfferTypeLive,
  ) as DataOfferLiveAzureStorageFormValue;

  return (
    value.offerLiveType === 'AZURE_STORAGE' && (
      <AzureStorageForm form={form} formKey={formKeyDataOfferTypeLive} />
    )
  );
};
