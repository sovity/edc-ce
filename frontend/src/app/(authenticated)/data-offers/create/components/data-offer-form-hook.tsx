/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  type DataOfferCreateFormModel,
  dataOfferFormSchema,
} from '@/app/(authenticated)/data-offers/create/components/data-offer-form-schema';
import {zodResolver} from '@hookform/resolvers/zod';
import {useForm} from 'react-hook-form';
import {useAssetIdGeneration} from '@/app/(authenticated)/data-offers/create/components/use-asset-id-generation';

export const useDataOfferCreateForm = (
  initialFormValue: DataOfferCreateFormModel,
) => {
  const form = useForm<DataOfferCreateFormModel>({
    mode: 'onTouched',
    resolver: zodResolver(dataOfferFormSchema),
    defaultValues: initialFormValue,
  });

  useAssetIdGeneration({
    form: form,
    assetIdFieldName: 'general.assetId',
    assetNameFieldName: 'general.title',
  });

  return {
    form,
    schema: dataOfferFormSchema,
  };
};
