/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
export const queryKeys = {
  config: {
    key: () => ['config'],
  },
  dashboard: {
    key: () => ['dashboard'],
  },
  transferHistory: {
    key: () => ['transferHistory'],
  },
  dataOffers: {
    key: () => ['dataOffers'],
    all: () => [...queryKeys.dataOffers.key(), 'all'],
    id: (dataOfferId: string) => [...queryKeys.dataOffers.key(), dataOfferId],

    listPage: () => [...queryKeys.dataOffers.all(), 'listPage'],

    detailPage: (dataOfferId: string) => [
      ...queryKeys.dataOffers.id(dataOfferId),
      dataOfferId,
    ],
  },
  contracts: {
    key: () => ['contracts'],
    id: (contractId: string) => [...queryKeys.contracts.key(), contractId],
    all: () => [...queryKeys.contracts.key(), 'all'],

    listPage: () => [...queryKeys.contracts.all(), 'listPage'],

    detailsPage: (contractId: string) => [
      ...queryKeys.contracts.id(contractId),
      'detailsPage',
    ],
  },
  policies: {
    key: () => ['policies'],
    all: () => [...queryKeys.policies.key(), 'all'],
    id: (policyDefinitionId: string) => [
      ...queryKeys.policies.key(),
      policyDefinitionId,
    ],

    listPage: () => [...queryKeys.policies.all(), 'listPage'],
  },
  assets: {
    key: () => ['assets'],
    all: () => [...queryKeys.assets.key(), 'all'],
    id: (assetId: string) => [...queryKeys.assets.key(), assetId],

    listPage: () => [...queryKeys.assets.all(), 'listPage'],
    detailsPage: (assetId: string) => [
      ...queryKeys.assets.id(assetId),
      'detailsPage',
    ],
  },
  contractNegotiation: {
    key: () => ['contractNegotiation'],
  },
  catalog: {
    key: () => ['catalog'],
    id: (participantId: string, endpointUrl: string) => [
      ...queryKeys.catalog.key(),
      participantId,
      endpointUrl,
    ],
    dataOffer: (
      participantId: string,
      endpointUrl: string,
      assetId: string,
    ) => [...queryKeys.catalog.id(participantId, endpointUrl), assetId],

    browserPage: () => [...queryKeys.catalog.key(), 'browserPage'],
    listPage: (participantId: string, endpointUrl: string) => [
      ...queryKeys.catalog.id(participantId, endpointUrl),
      'listPage',
    ],
    dataOfferDetails: (
      participantId: string,
      endpointUrl: string,
      assetId: string,
    ) => [
      ...queryKeys.catalog.dataOffer(participantId, endpointUrl, assetId),
      'dataOfferDetails',
    ],
  },
};
