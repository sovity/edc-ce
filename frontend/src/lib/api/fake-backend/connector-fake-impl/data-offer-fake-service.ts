/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  type DataOfferCreateRequest,
  DataOfferPublishType,
  type IdResponseDto,
  UiCriterionLiteralType,
} from '@sovity.de/edc-client';
import {assetIdAvailable, createAsset} from './asset-fake-service';
import {
  contractDefinitionIdAvailable,
  createContractDefinition,
} from './contract-definition-fake-service';
import {
  ALWAYS_TRUE_POLICY_ID,
  createPolicyDefinitionV2,
  policyDefinitionIdAvailable,
} from './policy-definition-fake-service';

const checkIdAvailability = (id: string): void => {
  if (
    !policyDefinitionIdAvailable(id).available ||
    !assetIdAvailable(id).available ||
    !contractDefinitionIdAvailable(id).available
  ) {
    throw new Error('Id already exists');
  }
};

const checkIfNoAlwaysTruePolicyExists = (): void => {
  if (policyDefinitionIdAvailable(ALWAYS_TRUE_POLICY_ID).available) {
    createPolicyDefinitionV2({
      policyDefinitionId: ALWAYS_TRUE_POLICY_ID,
      expression: {
        type: 'EMPTY',
      },
    });
  }
};

export const createDataOffer = (
  request: DataOfferCreateRequest,
): IdResponseDto => {
  const commonId = request.asset.id;
  let accessPolicyId = null;
  let contractPolicyId = null;

  checkIfNoAlwaysTruePolicyExists();
  checkIdAvailability(commonId);
  createAsset(request.asset);

  switch (request.publishType) {
    case DataOfferPublishType.DontPublish:
      return {id: commonId, lastUpdatedDate: new Date()};
    case DataOfferPublishType.PublishRestricted:
      createPolicyDefinitionV2({
        policyDefinitionId: commonId,
        expression: request.policyExpression!,
      });
      accessPolicyId = commonId;
      contractPolicyId = commonId;
      break;
    case DataOfferPublishType.PublishUnrestricted:
      accessPolicyId = ALWAYS_TRUE_POLICY_ID;
      contractPolicyId = ALWAYS_TRUE_POLICY_ID;
      break;
  }

  createContractDefinition({
    contractDefinitionId: commonId,
    accessPolicyId,
    contractPolicyId,
    assetSelector: [
      {
        operandLeft: commonId,
        operator: 'EQ',
        operandRight: {
          type: UiCriterionLiteralType.Value,
          value: commonId,
        },
      },
    ],
  });

  return {id: commonId, lastUpdatedDate: new Date()};
};
