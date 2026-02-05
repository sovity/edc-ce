/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {initiateTransferCustomSchema} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-custom-form';
import {initiateTransferHttpSchema} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-http-form';
import {z} from 'zod';
import {initiateTransferAzureStorageSchema} from './initiate-transfer-azure-storage-form';

export type InitiateTransferType = 'HTTP' | 'CUSTOM_JSON' | 'AZURE_STORAGE';

export const initiateTransferFormSchema = z.discriminatedUnion('transferType', [
  initiateTransferCustomSchema,
  initiateTransferHttpSchema,
  initiateTransferAzureStorageSchema,
]);

export type InitiateTransferFormValue = z.infer<
  typeof initiateTransferFormSchema
>;
