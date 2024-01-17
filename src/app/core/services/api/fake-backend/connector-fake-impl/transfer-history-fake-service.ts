import {
  TransferHistoryEntry,
  TransferHistoryPage,
  UiAsset,
} from '@sovity.de/edc-client';
import {getAssetById} from './asset-fake-service';
import {TestAssets} from './data/test-assets';

let transferHistoryEntries: TransferHistoryEntry[] = [
  {
    transferProcessId: '339b2a27-3b66-49f5-8b43-6a400d5914b5',
    createdDate: new Date('2023-03-20T11:18:59.659Z'),
    lastUpdatedDate: new Date('2023-07-25T11:18:59.659Z'),
    state: {
      code: 800,
      name: 'COMPLETED',
      simplifiedState: 'OK',
    },
    contractAgreementId: 'test-asset-1-cd:f52a5d30-6356-4a55-a75a-3c45d7a88c3e',
    direction: 'CONSUMING',
    counterPartyConnectorEndpoint: 'https://sovity-demo4-mds/api/v1/ids/data',
    counterPartyParticipantId: 'MDSL1234XX.C1234XX',
    assetName: 'test-asset-1',
    assetId: 'test-asset-1',
  },
  {
    transferProcessId: '1317d0da-cdc6-42ab-b54b-1f90bcfed508',
    createdDate: new Date('2023-01-20T11:18:59.659Z'),
    lastUpdatedDate: new Date('2023-03-25T11:18:59.659Z'),
    state: {
      code: -1,
      name: 'ERROR',
      simplifiedState: 'ERROR',
    },
    contractAgreementId: 'test-asset-2-cd:5816a60b-86c1-489a-b26a-ed129947f973',
    counterPartyParticipantId: 'MDSL1234XX.C1234XX',
    direction: 'CONSUMING',
    counterPartyConnectorEndpoint: 'http://edc2:11003/api/v1/ids/data',
    assetName: 'test-asset-2',
    assetId: 'test-asset-2',
    errorMessage:
      'TransferProcessManager: attempt #8 failed to send transfer. Retry limit exceeded, TransferProcess 1317d0da-cdc6-42ab-b54b-1f90bcfed508 moves to ERROR state. Cause: java.net.SocketException: Connection reset',
  },
  {
    transferProcessId: '81cdf4cf-8427-480f-9662-8a29d66ddd3b',
    createdDate: new Date('2022-03-25T11:18:59.659Z'),
    lastUpdatedDate: new Date('2022-11-20T11:18:59.659Z'),
    state: {
      code: 800,
      name: 'COMPLETED',
      simplifiedState: 'OK',
    },
    contractAgreementId: 'test-asset-3-cd:6ebbc301-9b1e-4cd7-9f17-97b5b7867531',
    direction: 'CONSUMING',
    counterPartyConnectorEndpoint: 'https://sovity-demo2-edc/api/v1/ids/data',
    counterPartyParticipantId: 'MDSL1234XX.C1234XX',
    assetName: 'test-asset-3',
    assetId: 'test-asset-3',
  },
  {
    transferProcessId: '47240a35-d8fc-41d9-b020-07b87f3cc7b6',
    createdDate: new Date('2022-01-29T11:18:59.659Z'),
    lastUpdatedDate: new Date('2022-02-24T11:18:59.659Z'),
    state: {
      code: 600,
      name: 'IN_PROGRESS',
      simplifiedState: 'RUNNING',
    },
    contractAgreementId: 'test-asset-4-cd:f52a5d30-6356-4a55-a75a-3c45d7a88c3e',
    direction: 'PROVIDING',
    counterPartyConnectorEndpoint: 'https://sovity-demo2-edc/api/v1/ids/data',
    counterPartyParticipantId: 'MDSL1234XX.C1234XX',
    assetName: TestAssets.full.title,
    assetId: TestAssets.full.assetId,
  },
];

export const transferHistoryPage = (): TransferHistoryPage => {
  return {
    transferEntries: transferHistoryEntries,
  };
};

export const transferProcessAsset = (transferProcessId: string): UiAsset => {
  const transfer = transferHistoryEntries.find(
    (it) => it.transferProcessId === transferProcessId,
  );
  const assetId = transfer?.assetId ?? 'unknown';
  const isProviding = transfer?.direction === 'PROVIDING';

  const dummyAsset: UiAsset = {
    assetId,
    title: assetId,
    participantId: 'unknown',
    connectorEndpoint: 'https://unknown/api/dsp',
    isOwnConnector: false,
    creatorOrganizationName: 'unknown',
  };

  const assetEntry = getAssetById(assetId);

  return isProviding && assetEntry
    ? TestAssets.toDummyAsset(assetEntry)
    : dummyAsset;
};
