import {
  ContractAgreementCard,
  ContractAgreementPage,
  ContractAgreementTransferProcess,
  IdResponseDto,
  InitiateTransferRequest,
} from '@sovity.de/edc-client';
import {Patcher, patchObj} from '../../../../utils/object-utils';
import {TestAssets} from './data/test-assets';
import {TestPolicies} from './data/test-policies';

let contractAgreements: ContractAgreementCard[] = [
  {
    contractAgreementId: 'my-own-asset-cd:f52a5d30-6356-4a55-a75a-3c45d7a88c3e',
    contractNegotiationId:
      'my-own-asset-neg:f52a5d30-6356-4a55-a75a-3c45d7a88c3e',
    direction: 'PROVIDING',
    counterPartyAddress: 'http://edc2:11003/api/v1/ids/data',
    counterPartyId: 'MDSL1234XX.C1234XX',
    contractSigningDate: new Date('2022-03-20T11:18:59.659Z'),
    asset: TestAssets.full,
    contractPolicy: TestPolicies.connectorRestricted,
    transferProcesses: [
      {
        transferProcessId: '2679d234-3340-44bf-a96b-c88b57838033',
        lastUpdatedDate: new Date('2023-04-24T12:34:52.896Z'),
        state: {
          code: 600,
          name: 'IN_PROGRESS',
          simplifiedState: 'RUNNING',
        },
      },
      {
        transferProcessId: 'c2863791-c8f3-49e7-8137-7fadaa36b4e4',
        lastUpdatedDate: new Date('2023-04-24T12:34:40.801Z'),
        state: {
          code: 800,
          name: 'COMPLETED',
          simplifiedState: 'OK',
        },
      },
      {
        transferProcessId: 'f3ee5129-1909-4d7b-a6fe-a25994d67b56',
        lastUpdatedDate: new Date('2023-04-24T12:34:36.735Z'),
        state: {
          code: 900,
          name: 'DEPROVISIONING',
          simplifiedState: 'OK',
        },
      },
      {
        transferProcessId: '2cf2c9be-3b8c-4768-b10e-c1d9f9874e62',
        lastUpdatedDate: new Date('2023-04-24T12:34:31.674Z'),
        state: {
          code: -1,
          name: 'ERROR',
          simplifiedState: 'ERROR',
        },
        errorMessage: 'Something went wrong!',
      },
    ],
  },
  {
    contractAgreementId:
      'my-test-asset-cd:6ebbc301-9b1e-4cd7-9f17-97b5b7867531',
    contractNegotiationId:
      'my-test-asset-neg:6ebbc301-9b1e-4cd7-9f17-97b5b7867531',
    direction: 'CONSUMING',
    counterPartyAddress: 'http://edc2:11003/api/v1/ids/data',
    counterPartyId: 'MDSL1234XX.C1234XX',
    contractSigningDate: new Date('2022-03-25T11:18:59.659Z'),
    asset: TestAssets.toDummyAsset(TestAssets.boring),
    contractPolicy: TestPolicies.connectorRestricted,
    transferProcesses: [
      {
        transferProcessId: '522138de-349d-4b68-9356-7e5929f053e0',
        lastUpdatedDate: new Date('2023-04-24T12:32:43.027Z'),
        state: {
          code: 800,
          name: 'COMPLETED',
          simplifiedState: 'OK',
        },
      },
    ],
  },
  {
    contractAgreementId:
      'my-test-asset-2-cd:6ebbc301-9b1e-4cd7-9f17-08b5b7867533',
    contractNegotiationId:
      'my-test-asset-2-neg:6ebbc301-9b1e-4cd7-9f17-08b5b7867533',
    direction: 'CONSUMING',
    counterPartyAddress: 'http://edc2:11003/api/v1/ids/data',
    counterPartyId: 'MDSL1234XX.C1234XX',
    contractSigningDate: new Date('2022-03-25T11:18:59.659Z'),
    asset: TestAssets.toDummyAsset(TestAssets.boring),
    contractPolicy: TestPolicies.connectorRestricted,
    transferProcesses: [],
  },
  {
    contractAgreementId: 'my-test-asset-cd:6ebbc301-9b1e-4cd7-9f17-97b5b786752',
    contractNegotiationId:
      'my-test-asset-neg:6ebbc301-9b1e-4cd7-9f17-97b5b786752',
    direction: 'CONSUMING',
    counterPartyAddress: 'http://edc2:11003/api/v1/ids/data',
    counterPartyId: 'MDSL1234XX.C1234XX',
    contractSigningDate: new Date('2022-03-25T11:18:59.659Z'),
    asset: TestAssets.toDummyAsset(TestAssets.boring),
    contractPolicy: TestPolicies.connectorRestricted,
    transferProcesses: [
      {
        transferProcessId: '522138de-349d-4b68-9356-7e5929f053e0',
        lastUpdatedDate: new Date('2023-04-24T12:32:43.027Z'),
        state: {
          code: 800,
          name: 'COMPLETED',
          simplifiedState: 'OK',
        },
      },
    ],
  },
];
export const contractAgreementPage = (): ContractAgreementPage => {
  return {
    contractAgreements,
  };
};

export const addContractAgreement = (
  contractAgreement: ContractAgreementCard,
) => {
  contractAgreements = [contractAgreement, ...contractAgreements];
};

export const contractAgreementInitiateTransfer = (
  request: InitiateTransferRequest,
): IdResponseDto => {
  const contractAgreementId = request?.contractAgreementId ?? '';
  const transferProcessId =
    'transfer-process-' + Math.random().toString().substring(2);

  const newTransferProcess: ContractAgreementTransferProcess = {
    transferProcessId,
    state: {
      code: 800,
      name: 'COMPLETED',
      simplifiedState: 'OK',
    },
    lastUpdatedDate: new Date(),
  };

  updateAgreement(contractAgreementId, (agremeent) => ({
    transferProcesses: [newTransferProcess, ...agremeent.transferProcesses],
  }));

  return {id: transferProcessId, lastUpdatedDate: new Date(new Date())};
};

const updateAgreement = (
  contractAgreementId: string,
  patcher: Patcher<ContractAgreementCard>,
) => {
  contractAgreements = contractAgreements.map((agreement) =>
    agreement.contractAgreementId === contractAgreementId
      ? patchObj(agreement, patcher)
      : agreement,
  );
};
