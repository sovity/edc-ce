import {
  IdResponseDto,
  PolicyDefinitionCreateRequest,
  PolicyDefinitionDto,
  PolicyDefinitionPage,
} from '@sovity.de/edc-client';
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
];
export const policyDefinitionPage = (): PolicyDefinitionPage => {
  return {policies: policyDefinitions};
};

export const getPolicyDefinitionByJsonLd = (jsonLd: string) =>
  policyDefinitions.find((it) => it.policy.policyJsonLd === jsonLd)?.policy;

export const createPolicyDefinition = (
  request: PolicyDefinitionCreateRequest,
): IdResponseDto => {
  const newPolicyDefinition: PolicyDefinitionDto = {
    policyDefinitionId: request.policyDefinitionId,
    policy: {
      constraints: request.policy.constraints,
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
