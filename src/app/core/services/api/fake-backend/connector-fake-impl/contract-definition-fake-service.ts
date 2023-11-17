import {
  ContractDefinitionEntry,
  ContractDefinitionPage,
  ContractDefinitionRequest,
  IdResponseDto,
} from '@sovity.de/edc-client';
import {AssetProperty} from '../../../models/asset-properties';

let contractDefinitions: ContractDefinitionEntry[] = [
  {
    contractDefinitionId: 'test-contract-definition-1',
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
