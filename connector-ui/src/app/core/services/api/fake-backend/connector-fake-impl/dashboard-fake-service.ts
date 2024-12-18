import {DashboardPage} from '@sovity.de/edc-client';

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
  connectorParticipantId: 'MDSL1234XX.C1234XX',
  connectorTitle: 'My Connector',
  connectorDescription: 'Example Connector with Fake Backend',
  connectorMaintainerName: 'sovity GmbH',
  connectorMaintainerUrl: 'https://sovity.de',
  connectorCuratorName: 'Example GmbH',
  connectorCuratorUrl: 'https://example.com',
  connectorEndpoint: 'https://edc.fake-backend/api/dsp',
  connectorMiwConfig: {
    url: 'https://miw.fake-backend',
    tokenUrl: 'https://miw.fake-backend/token',
    authorityId: 'fake-miw',
  },
  connectorDapsConfig: {
    tokenUrl: 'https://daps.fake-backend/token',
    jwksUrl: 'https://daps.fake-backend/jwks.json',
  },
});
