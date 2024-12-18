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
