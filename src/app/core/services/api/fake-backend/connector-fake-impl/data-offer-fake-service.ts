import {
  DataOfferCreationRequest,
  DataOfferCreationRequestPolicyEnum,
  IdResponseDto,
  UiCriterionLiteralType,
} from '@sovity.de/edc-client';
import {ALWAYS_TRUE_POLICY_ID} from '../../../../../shared/business/edit-asset-form/form/model/always-true-policy-id';
import {assetIdAvailable, createAsset} from './asset-fake-service';
import {
  contractDefinitionIdAvailable,
  createContractDefinition,
} from './contract-definition-fake-service';
import {
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

export const createDataOffer = (
  request: DataOfferCreationRequest,
): IdResponseDto => {
  const commonId = request.uiAssetCreateRequest.id;
  let accessPolicyId = null;
  let contractPolicyId = null;

  checkIdAvailability(commonId);
  createAsset(request.uiAssetCreateRequest);

  switch (request.policy) {
    case DataOfferCreationRequestPolicyEnum.DontPublish:
      return {id: commonId, lastUpdatedDate: new Date()};
    case DataOfferCreationRequestPolicyEnum.PublishRestricted:
      createPolicyDefinitionV2({
        policyDefinitionId: commonId,
        expression: request.uiPolicyExpression!,
      });
      accessPolicyId = commonId;
      contractPolicyId = commonId;
      break;
    case DataOfferCreationRequestPolicyEnum.PublishUnrestricted:
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
