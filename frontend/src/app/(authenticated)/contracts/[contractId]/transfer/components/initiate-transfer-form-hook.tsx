/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  initiateTransferFormSchema,
  type InitiateTransferFormValue,
} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-form-schema';
import {zodResolver} from '@hookform/resolvers/zod';
import {useForm} from 'react-hook-form';

export const useInitiateTransferForm = () => {
  const defaultHttp: InitiateTransferFormValue = {
    transferType: 'HTTP',
    httpMethod: 'POST',
    httpUrl: '',
    httpAdditionalHeaders: [],
    auth: {type: 'NONE'},
  };

  const defaultCustom: InitiateTransferFormValue = {
    transferType: 'CUSTOM_JSON',
    dataAddressJson: '',
  };

  return {
    form: useForm<InitiateTransferFormValue>({
      mode: 'onTouched',
      resolver: zodResolver(initiateTransferFormSchema),
      defaultValues: {
        ...defaultCustom,
        ...defaultHttp,
      },
    }),
    schema: initiateTransferFormSchema,
  };
};
