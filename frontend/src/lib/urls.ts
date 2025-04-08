/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
const encodeParams = (...params: string[]): string[] => {
  return params.map((param) => encodeURIComponent(param));
};

const addSelector = (url: string, selector?: string) => {
  if (selector) {
    return `${url}#${selector}`;
  }
  return url;
};

export const urls = {
  rootPage: () => '/',
  transferHistory: () => '/transfer-history',
  assets: {
    listPage: () => '/assets',
    createPage: () => '/assets/create',
    detailPage: (assetId: string, tab = 'overview') => {
      const [encodedAssetId, encodedTab] = encodeParams(assetId, tab);
      return `/assets/${encodedAssetId}/details/${encodedTab}`;
    },
    editPage: (assetId: string) => {
      const [encodedAssetId] = encodeParams(assetId);
      return `/assets/${encodedAssetId}/edit`;
    },
  },
  catalog: {
    browserPage: () => '/catalog',
    listPage: (participantId: string, connectorEndpoint: string) => {
      const [encodedParticipantId, encodedConnectorEndpoint] = encodeParams(
        participantId,
        connectorEndpoint,
      );
      return `/catalog/${encodedParticipantId}/${encodedConnectorEndpoint}`;
    },
    detailPage: (
      participantId: string,
      endpointUrl: string,
      assetId: string,
      tab = 'overview',
      selector?: string,
    ) => {
      const [encodedParticipantId, encodedEndpointUrl, encodedAssetId] =
        encodeParams(participantId, endpointUrl, assetId);
      return addSelector(
        `/catalog/${encodedParticipantId}/${encodedEndpointUrl}/${encodedAssetId}/${tab}`,
        selector,
      );
    },
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
    detailPage: (contractId: string, tab = 'contract-agreement') => {
      const [encodedContractId] = encodeParams(contractId);
      return `/contracts/${encodedContractId}/details/${tab}`;
    },
    transferPage: (contractId: string) =>
      `/contracts/${encodeURIComponent(contractId)}/transfer`,
  },
};
