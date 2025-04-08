/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type DashboardPage} from '@sovity.de/edc-client';

export const dashboardPage = (): DashboardPage => ({
  numAssets: 4,
  numContractAgreementsConsuming: 10,
  numContractAgreementsProviding: 123,
  numPolicies: 4,
  numContractDefinitions: 4,
  transferProcessesConsuming: {
    numTotal: 8,
    numOk: 5,
    numRunning: 1,
    numError: 2,
  },
  transferProcessesProviding: {
    numTotal: 2,
    numOk: 2,
    numError: 0,
    numRunning: 0,
  },
  connectorParticipantId: 'BPNL1234XX.C1234XX',
  connectorTitle: 'My Connector',
  connectorDescription: 'Example Connector with Fake Backend',
  connectorMaintainerName: 'sovity GmbH',
  connectorMaintainerUrl: 'https://sovity.de',
  connectorCuratorName: 'Example GmbH',
  connectorCuratorUrl: 'https://example.com',
  connectorEndpoint: 'https://edc.fake-backend/api/dsp',
  managementApiUrlShownInDashboard: 'https://edc.fake-backend/api/management',
  connectorCxDidConfig: {
    myDid: 'your-did-long-number:1234567890:idk',
    bdrsUrl: 'https://bdrs.fake-backend',
    dimUrl: 'https://dim.fake-backend',
    trustedVcIssuer: 'https://trusted-vc-issuer.fake-backend',
    walletTokenUrl:
      'https://wallet-token.fake-backend/example-for-a-really-long-value-that-will-definitely-grow-too-large',
  },
  connectorDapsConfig: {
    tokenUrl: 'https://daps.fake-backend/token',
    jwksUrl: 'https://daps.fake-backend/jwks.json',
  },
});
