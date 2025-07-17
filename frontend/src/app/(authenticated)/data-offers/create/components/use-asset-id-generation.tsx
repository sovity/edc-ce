/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useEffect, useState} from 'react';
import {type UseFormReturn} from 'react-hook-form';
import {type DataOfferFormMode} from './data-offer-form-mode';
import {useAsyncIdValidation} from './use-async-id-validation';

export const useAssetIdGeneration = ({
  form,
  assetIdFieldName,
  assetNameFieldName,
  formMode,
}: {
  form: UseFormReturn<any>;
  assetIdFieldName: string;
  assetNameFieldName: string;
  formMode: DataOfferFormMode;
}) => {
  const [oldAssetName, setOldAssetName] = useState<string>('');
  const assetId = form.watch(assetIdFieldName) as string;
  const assetName = form.watch(assetNameFieldName) as string;

  useAsyncIdValidation({
    form,
    assetIdFieldName,
    assetId,
    formMode,
  });

  useEffect(() => {
    if (assetName == oldAssetName) {
      return;
    }

    if (formMode === 'EDIT' && assetId) {
      return;
    }

    const oldAssetId = generateAssetId(oldAssetName);
    const newAssetId = generateAssetId(assetName);

    if (assetId === oldAssetId && newAssetId !== assetId) {
      form.setValue(assetIdFieldName, newAssetId);
      form.clearErrors(assetIdFieldName);
    }

    setOldAssetName(assetName);
  }, [form, assetId, assetIdFieldName, assetName, oldAssetName, formMode]);
};

const generateAssetId = (assetName: string | null): string => {
  if (!assetName) {
    return '';
  }

  return (assetName ?? '')
    .trim()
    .replace(':', '-')
    .replaceAll(' ', '-')
    .toLowerCase();
};
