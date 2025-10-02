/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

'use client';

import {api} from '@/lib/api/client';
import {type DataOfferFormMode} from './data-offer-form-mode';
import debounce from 'lodash.debounce';

export const validateAssetId = () =>
  debounce(isAssetIdAvailable, 1000, {leading: true, maxWait: 1000});

const isAssetIdAvailable = async (
  assetId: string,
  formMode: DataOfferFormMode,
  publishingMode: string,
): Promise<boolean> => {
  if (formMode === 'EDIT') {
    return true;
  }

  const assetIdAvailable =
    (await api.uiApi.isAssetIdAvailable({assetId})).available ?? false;
  if (formMode === 'CREATE_ASSET' || publishingMode === 'DONT_PUBLISH') {
    return assetIdAvailable;
  }

  const contractDefinitionIdAvailable =
    (
      await api.uiApi.isContractDefinitionIdAvailable({
        contractDefinitionId: assetId,
      })
    ).available ?? false;
  if (publishingMode === 'PUBLISH_UNRESTRICTED') {
    return assetIdAvailable && contractDefinitionIdAvailable;
  }

  const policyIdAvailable =
    (await api.uiApi.isPolicyIdAvailable({policyId: assetId})).available ??
    false;
  if (publishingMode === 'PUBLISH_RESTRICTED') {
    return (
      assetIdAvailable && contractDefinitionIdAvailable && policyIdAvailable
    );
  }

  return false;
};
