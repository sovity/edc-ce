/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  type ContractDefinitionEntry,
  type ContractDefinitionPage,
  type ContractDefinitionRequest,
  type IdAvailabilityResponse,
  type IdResponseDto,
} from '@sovity.de/edc-client';

const EDC = 'https://w3id.org/edc/v0.0.1/ns/';

export const AssetProperty = {
  id: `${EDC}id`,
};

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
  {
    contractDefinitionId: 'test-data-offer-2',
    contractPolicyId: 'test-policy-definition-2',
    accessPolicyId: 'test-policy-definition-2',
    assetSelector: [
      {
        operandLeft: AssetProperty.id,
        operator: 'EQ',
        operandRight: {type: 'VALUE', value: 'test-asset-2'},
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
  const newEntry: ContractDefinitionEntry = {
    contractDefinitionId: request.contractDefinitionId,
    contractPolicyId: request.contractPolicyId,
    accessPolicyId: request.accessPolicyId,
    assetSelector: request.assetSelector,
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
