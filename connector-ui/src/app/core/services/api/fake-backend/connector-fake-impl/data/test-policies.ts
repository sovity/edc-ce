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
import {UiPolicy, UiPolicyExpression} from '@sovity.de/edc-client';
import {policyLeftExpressions} from '../../../../../../shared/business/policy-editor/model/policy-left-expressions';
import {
  constraint,
  constraintList,
  multi,
} from '../../../../../../shared/business/policy-editor/model/ui-policy-expression-utils';

export namespace TestPolicies {
  const policy = (
    expression: UiPolicyExpression,
    errors: string[] = [],
  ): UiPolicy => ({
    policyJsonLd: JSON.stringify({
      _description:
        'The actual JSON-LD will look different. This is just data from the fake backend.',
      expression,
    }),
    expression,
    errors,
  });

  export const connectorRestricted: UiPolicy = policy(
    multi(
      'AND',
      constraint(
        policyLeftExpressions.policyEvaluationTime,
        'GEQ',
        '2020-11-30T23:00:00.000Z',
      ),
      constraint(
        policyLeftExpressions.policyEvaluationTime,
        'LT',
        '2020-12-07T23:00:00.000Z',
      ),
      multi(
        'OR',
        constraint('REFERRING_CONNECTOR', 'EQ', 'MDSL1234XX.C1234XX'),
        constraint('REFERRING_CONNECTOR', 'EQ', 'MDSL1234XX.C1235YY'),
      ),
      constraint('ALWAYS_TRUE', 'EQ', 'true'),
    ),
  );

  export const warnings: UiPolicy = policy(
    constraintList('SOME_UNKNOWN_PROP', 'HAS_PART', ['A', 'B', 'C']),
    ['$.duties: Duties are currently unsupported.'],
  );
  export const failedMapping: UiPolicy = policy({
    type: 'EMPTY',
  });

  export const unrestricted: UiPolicy = policy({
    type: 'EMPTY',
  });
}
