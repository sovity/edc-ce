/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type UseFormReturn} from 'react-hook-form';
import {useEffect, useState} from 'react';

export const useAssetIdGeneration = ({
  form,
  assetIdFieldName,
  assetNameFieldName,
}: {
  form: UseFormReturn<any>;
  assetIdFieldName: string;
  assetNameFieldName: string;
}) => {
  const [oldAssetName, setOldAssetName] = useState<string>('');
  const assetId = form.watch(assetIdFieldName) as string;
  const assetName = form.watch(assetNameFieldName) as string;

  useEffect(() => {
    if (assetName == oldAssetName) {
      return;
    }

    const oldAssetId = generateAssetId(oldAssetName);
    const newAssetId = generateAssetId(assetName);

    if (assetId === oldAssetId && newAssetId !== assetId) {
      form.setValue(assetIdFieldName, newAssetId);
      form.clearErrors(assetIdFieldName);
    }

    setOldAssetName(assetName);
  }, [form, assetId, assetIdFieldName, assetName, oldAssetName]);
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
