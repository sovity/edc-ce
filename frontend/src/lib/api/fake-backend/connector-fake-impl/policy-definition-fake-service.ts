/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {
  type IdAvailabilityResponse,
  type IdResponseDto,
  type PolicyDefinitionCreateDto,
  type PolicyDefinitionCreateRequest,
  type PolicyDefinitionDto,
  type PolicyDefinitionPage,
  type UiPolicyExpression,
} from '@sovity.de/edc-client';
import {TestPolicies} from './data/test-policies';

export const ALWAYS_TRUE_POLICY_ID = 'always-true';

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
