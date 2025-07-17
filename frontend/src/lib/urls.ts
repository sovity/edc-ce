/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

import {encodeParam} from '@/lib/utils/http-utils';

const addSelector = (url: string, selector?: string) => {
  if (selector) {
    return `${url}#${selector}`;
  }
  return url;
};

// shorthand for faster access
const e = encodeParam;

export const urls = {
  rootPage: () => '/',
  transferHistory: () => '/transfer-history',
  assets: {
    listPage: () => '/assets',
    createPage: () => '/assets/create',
    detailPage: (assetId: string, tab = 'overview') =>
      `/assets/${e(assetId)}/details/${e(tab)}`,
    editPage: (assetId: string) => `/assets/${e(assetId)}/edit`,
  },
  vaultSecrets: {
    listPage: () => '/vault-secrets',
    createPage: () => '/vault-secrets/create',
    editPage: (id: string) => `/vault-secrets/${e(id)}/edit`,
  },
  catalog: {
    browserPage: () => '/catalog',
    listPage: (participantId: string, connectorEndpoint: string) =>
      `/catalog/${e(participantId)}/${e(connectorEndpoint)}`,
    detailPage: (
      participantId: string,
      endpointUrl: string,
      assetId: string,
      tab = 'overview',
      selector?: string,
    ) =>
      addSelector(
        `/catalog/${e(participantId)}/${e(endpointUrl)}/${e(assetId)}/${e(tab)}`,
        selector,
      ),
  },
  dataOffers: {
    listPage: () => '/data-offers',
    createPage: () => '/data-offers/create',
    publishPage: () => '/data-offers/publish',
  },
  policies: {
    listPage: () => '/policies',
    createPage: () => '/policies/create',
  },
  contracts: {
    listPage: () => '/contracts',
    detailPage: (contractId: string, tab = 'contract-agreement') =>
      `/contracts/${e(contractId)}/details/${e(tab)}`,
    transferPage: (contractId: string) =>
      `/contracts/${e(contractId)}/transfer`,
  },
};
