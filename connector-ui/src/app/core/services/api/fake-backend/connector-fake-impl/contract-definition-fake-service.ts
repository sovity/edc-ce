import {
  ContractDefinitionEntry,
  ContractDefinitionPage,
  ContractDefinitionRequest,
  IdAvailabilityResponse,
  IdResponseDto,
} from '@sovity.de/edc-client';
import {AssetProperty} from '../../../models/asset-properties';

let contractDefinitions: ContractDefinitionEntry[] = [
  {
    contractDefinitionId: 'test-data-offer-1',
    contractPolicyId: 'test-policy-definition-1',
    accessPolicyId: 'test-policy-definition-1',
    assetSelector: [
      {
        operandLeft: AssetProperty.id,
        operator: 'EQ',
        operandRight: {type: 'VALUE', value: 'test-asset-1'},
      },
    ],
  },
];

export const contractDefinitionPage = (): ContractDefinitionPage => {
  return {
    contractDefinitions,
  };
};

export const contractDefinitionIdAvailable = (
  contractDefinitionId: string,
): IdAvailabilityResponse => {
  return {
    id: contractDefinitionId,
    available: !contractDefinitions.some(
      (it) => it.contractDefinitionId === contractDefinitionId,
    ),
  };
};

export const createContractDefinition = (
  request: ContractDefinitionRequest,
): IdResponseDto => {
  let newEntry: ContractDefinitionEntry = {
    contractDefinitionId: request.contractDefinitionId!,
    contractPolicyId: request.contractPolicyId!,
    accessPolicyId: request.accessPolicyId!,
    assetSelector: request.assetSelector!,
  };

  contractDefinitions = [newEntry, ...contractDefinitions];

  return {
    id: newEntry.contractDefinitionId,
    lastUpdatedDate: new Date(),
  };
};

export const deleteContractDefinition = (id: string): IdResponseDto => {
  contractDefinitions = contractDefinitions.filter(
    (it) => it.contractDefinitionId !== id,
  );
  return {id, lastUpdatedDate: new Date()};
};
