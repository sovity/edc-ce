import {
  IdResponseDto,
  PolicyDefinitionCreateRequest,
  PolicyDefinitionDto,
  PolicyDefinitionPage,
} from '@sovity.de/edc-client';

export let policyDefinitions: PolicyDefinitionDto[] = [
  {
    policyDefinitionId: 'test-policy-definition-1',
    policy: {
      policyJsonLd: '{"example-policy-jsonld": true}',
      constraints: [
        {
          left: 'REFERRING_CONNECTOR',
          operator: 'EQ',
          right: {type: 'STRING', value: 'https://my-other-connector'},
        },
      ],
      errors: [],
    },
  },
  {
    policyDefinitionId: 'test-policy-definition-1-with-errors',
    policy: {
      policyJsonLd: '{"example-policy-jsonld": true}',
      constraints: [
        {
          left: 'REFERRING_CONNECTOR',
          operator: 'EQ',
          right: {type: 'STRING', value: 'https://my-other-connector'},
        },
      ],
      errors: ['test-error-1'],
    },
  },
  {
    policyDefinitionId: 'test-policy-definition-3',
    policy: {
      policyJsonLd: '{"example-policy-jsonld": true}',
      constraints: [],
      errors: ['No constraints found!'],
    },
  },
];
export const policyDefinitionPage = (): PolicyDefinitionPage => {
  return {policies: policyDefinitions};
};

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
