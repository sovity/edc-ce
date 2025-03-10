/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {UiDataOffer} from '@sovity.de/edc-client';
import {ContractOffer} from './contract-offer';
import {UiAssetMapped} from './ui-asset-mapped';

/**
 * Contract Offer (UI Dto)
 */
export type DataOffer = Omit<UiDataOffer, 'asset' | 'contractOffers'> & {
  asset: UiAssetMapped;
  contractOffers: ContractOffer[];
};
