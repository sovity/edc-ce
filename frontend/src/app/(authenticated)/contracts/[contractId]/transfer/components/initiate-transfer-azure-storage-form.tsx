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
import { AzureDataSinkForm, azureDataSinkSchema } from '@/app/(authenticated)/components/azure-data-sink-form';
import { type InitiateTransferFormValue } from './initiate-transfer-form-schema';

export const initiateTransferAzureStorageSchema = azureDataSinkSchema.extend({
  transferType: z.literal('AZURE_STORAGE' satisfies DataOfferLiveType),
});

export type InitiateTransferAzureStorageFormValue = z.infer<
  typeof initiateTransferAzureStorageSchema
>;

export const InitiateTransferAzureStorageForm = ({
  form,
}: {
  form: UseFormReturn<InitiateTransferFormValue>;
}) => {
  const value = form.watch();

  return (
    value.transferType === 'AZURE_STORAGE' && (
      <AzureDataSinkForm form={form} />
    )
  );
};
