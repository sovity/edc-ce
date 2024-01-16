import {
  ConnectorDetailPageQuery,
  ConnectorDetailPageResult,
  ConnectorListEntry,
  ConnectorPageQuery,
  ConnectorPageResult,
} from '@sovity.de/broker-server-client';
import {subMinutes} from 'date-fns';

const CONNECTORS: ConnectorDetailPageResult[] = [
  {
    participantId: 'MDSL1234XX.C1234XX',
    endpoint: 'https://example-connector/api/dsp',
    organizationName: 'Organization A',
    createdAt: new Date('2023-04-01'),
    lastSuccessfulRefreshAt: subMinutes(new Date(), 5),
    lastRefreshAttemptAt: subMinutes(new Date(), 5),
    onlineStatus: 'ONLINE',
    numDataOffers: 5,
    connectorCrawlingTimeAvg: 400,
  },
  {
    participantId: 'MDSL1235XX.C1235XX',
    endpoint: 'https://example-connector2/api/dsp',
    organizationName: 'Organization B',
    createdAt: new Date('2023-04-01'),
    lastSuccessfulRefreshAt: subMinutes(new Date(), 5),
    lastRefreshAttemptAt: subMinutes(new Date(), 5),
    onlineStatus: 'OFFLINE',
    numDataOffers: 0,
    connectorCrawlingTimeAvg: 400,
  },
  {
    participantId: 'MDSL1236XX.C1237XX',
    endpoint: 'https://example-connector3/api/dsp',
    organizationName: 'Organization C',
    createdAt: new Date('2023-04-01'),
    lastSuccessfulRefreshAt: subMinutes(new Date(), 5),
    lastRefreshAttemptAt: subMinutes(new Date(), 5),
    onlineStatus: 'DEAD',
    numDataOffers: 0,
    connectorCrawlingTimeAvg: 400,
  },
];

export const getConnectorPage = (
  query: ConnectorPageQuery,
): ConnectorPageResult => {
  const connectors: ConnectorListEntry[] = CONNECTORS.map(
    buildConnectorListEntry,
  );

  return {
    connectors,
    paginationMetadata: {
      pageSize: 20,
      pageOneBased: 0,
      numTotal: connectors.length,
      numVisible: connectors.length,
    },
    availableSortings: [
      {sorting: 'TITLE', title: 'Test Sorting'},
      {sorting: 'ONLINE_STATUS', title: 'Other Sorting'},
    ],
  };
};

export const getConnectorDetailPage = (
  query: ConnectorDetailPageQuery,
): ConnectorDetailPageResult => {
  return CONNECTORS.find((it) => it.endpoint === query.connectorEndpoint)!;
};

const buildConnectorListEntry = (
  it: ConnectorDetailPageResult,
): ConnectorListEntry => ({
  onlineStatus: it.onlineStatus,
  createdAt: it.createdAt,
  endpoint: it.endpoint,
  organizationName: it.organizationName,
  lastSuccessfulRefreshAt: it.lastSuccessfulRefreshAt,
  lastRefreshAttemptAt: it.lastRefreshAttemptAt,
  numDataOffers: it.numDataOffers,
  participantId: it.participantId,
});
