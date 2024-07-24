import {
  IdAvailabilityResponse,
  IdResponseDto,
  PolicyDefinitionCreateDto,
  PolicyDefinitionCreateRequest,
  PolicyDefinitionDto,
  PolicyDefinitionPage,
  UiPolicyExpression,
} from '@sovity.de/edc-client';
import {ALWAYS_TRUE_POLICY_ID} from 'src/app/component-library/edit-asset-form/edit-asset-form/form/model/always-true-policy-id';
import {TestPolicies} from './data/test-policies';

let policyDefinitions: PolicyDefinitionDto[] = [
  {
    policyDefinitionId: 'test-policy-definition-1',
    policy: TestPolicies.connectorRestricted,
  },
  {
    policyDefinitionId: 'test-policy-definition-2',
    policy: TestPolicies.warnings,
  },
  {
    policyDefinitionId: 'test-policy-definition-3',
    policy: TestPolicies.failedMapping,
  },
  {
    policyDefinitionId: ALWAYS_TRUE_POLICY_ID,
    policy: TestPolicies.unrestricted,
  },
];
export const policyDefinitionPage = (): PolicyDefinitionPage => {
  return {policies: policyDefinitions};
};

export const policyDefinitionIdAvailable = (
  policyDefinitionId: string,
): IdAvailabilityResponse => {
  return {
    id: policyDefinitionId,
    available: !policyDefinitions.some(
      (it) => it.policyDefinitionId === policyDefinitionId,
    ),
  };
};

export const getPolicyDefinitionByJsonLd = (jsonLd: string) =>
  policyDefinitions.find((it) => it.policy.policyJsonLd === jsonLd)?.policy;

export const createPolicyDefinition = (
  request: PolicyDefinitionCreateRequest,
): IdResponseDto => {
  const expression: UiPolicyExpression = {
    type: 'AND',
    expressions: (request.policy.constraints ?? []).map((it) => ({
      type: 'CONSTRAINT',
      constraint: it,
    })),
  };

  return createPolicyDefinitionV2({
    policyDefinitionId: request.policyDefinitionId,
    expression,
  });
};

export const createPolicyDefinitionV2 = (
  request: PolicyDefinitionCreateDto,
): IdResponseDto => {
  const newPolicyDefinition: PolicyDefinitionDto = {
    policyDefinitionId: request.policyDefinitionId,
    policy: {
      expression: request.expression,
      errors: [],
      policyJsonLd: '{"example-policy-jsonld": true}',
    },
  };
  policyDefinitions = [newPolicyDefinition, ...policyDefinitions];

  return {
    id: request.policyDefinitionId,
    lastUpdatedDate: new Date(),
  };
};

export const deletePolicyDefinition = (id: string): IdResponseDto => {
  policyDefinitions = policyDefinitions.filter(
    (it) => it.policyDefinitionId !== id,
  );
  return {id, lastUpdatedDate: new Date()};
};
