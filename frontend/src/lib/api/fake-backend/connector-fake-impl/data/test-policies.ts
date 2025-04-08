/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  constraint,
  constraintList,
  multi,
} from '@/components/policy-editor/core/ui-policy-expression-utils';
import {type UiPolicy, type UiPolicyExpression} from '@sovity.de/edc-client';

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
      constraint('POLICY_EVALUATION_TIME', 'GEQ', '2020-11-30T23:00:00.000Z'),
      constraint('POLICY_EVALUATION_TIME', 'LT', '2020-12-07T23:00:00.000Z'),
      multi(
        'OR',
        constraint('REFERRING_CONNECTOR', 'EQ', 'BPNL1234XX.C1234XX'),
        constraint('REFERRING_CONNECTOR', 'EQ', 'BPNL1234XX.C1235YY'),
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
