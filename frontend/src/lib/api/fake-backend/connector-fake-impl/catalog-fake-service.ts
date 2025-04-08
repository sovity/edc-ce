/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type UiDataOffer} from '@sovity.de/edc-client';
import {TestAssets} from './data/test-assets';
import {TestPolicies} from './data/test-policies';

const dataOffers: UiDataOffer[] = [
  {
    endpoint: 'https://existing-other-connector/api/dsp',
    participantId: 'BPNL1234XX.C1234XX',
    asset: TestAssets.full,
    contractOffers: [
      {
        contractOfferId:
          'dGVzdHRlc3R0ZXN0dGVzdHRlc3Q=:dGVzdHRlc3R0ZXN0dGVzdHRlc3Q=:MDE5MjA4ZWMtMjI0My03YmEyLWE5ZGYtYzRjZTZkZDEyYzAx',
        policy: TestPolicies.connectorRestricted,
      },
      {
        contractOfferId:
          'Zmlyc3QtY2Q=:Zmlyc3QtYXNzZXQtMS4w:MjgzNTZkMTMtN2ZhYy00NTQwLTgwZjItMjI5NzJjOTc1ZWNi',
        policy: TestPolicies.warnings,
      },
    ],
  },
  {
    endpoint: 'https://existing-other-connector/api/dsp',
    participantId: 'BPNL1234XX.C1234XX',
    asset: TestAssets.onRequestAsset,
    contractOffers: [
      {
        contractOfferId: 'on-request-contract-offer',
        policy: TestPolicies.failedMapping,
      },
    ],
  },
  {
    endpoint: 'https://existing-other-connector/api/dsp',
    asset: TestAssets.boring,
    participantId: 'BPNL1234XX.C1234XX',
    contractOffers: [
      {
        contractOfferId: 'test-contract-offer-2',
        policy: TestPolicies.failedMapping,
      },
    ],
  },
  {
    endpoint: 'https://existing-other-connector/api/dsp',
    asset: TestAssets.short,
    participantId: 'BPNL1234XX.C1234XX',
    contractOffers: [
      {
        contractOfferId: 'test-contract-offer-3',
        policy: TestPolicies.failedMapping,
      },
    ],
  },
];

export const getCatalogPageDataOffers = (
  connectorEndpoint: string,
): UiDataOffer[] => {
  return dataOffers
    .filter((it) => it.endpoint === connectorEndpoint)
    .map((it) => ({
      ...it,
      contractOffers: it.contractOffers.map((contractOffer) => ({
        ...contractOffer,
        // Simulate real backend where the Contract Offer ID is ever-changing ephemeral bullshit for JSON-LD
        contractOfferId: `${contractOffer.contractOfferId}.${Math.random().toString().substring(2)}`,
      })),
    }));
};
