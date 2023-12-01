import {UiPolicy, UiPolicyConstraint} from '@sovity.de/edc-client';

export namespace TestPolicies {
  const policy = (
    constraints: UiPolicyConstraint[],
    errors: string[] = [],
  ) => ({
    policyJsonLd: JSON.stringify({
      _description:
        'The actual JSON-LD will look different. This is just data from the fake backend.',
      constraints,
    }),
    constraints,
    errors,
  });

  export const connectorRestricted: UiPolicy = policy([
    {
      left: 'REFERRING_CONNECTOR',
      operator: 'EQ',
      right: {type: 'STRING', value: 'MDSL1234XX.C1234XX'},
    },
  ]);

  export const warnings: UiPolicy = policy(
    [
      {
        left: 'SOME_UNKNOWN_PROP',
        operator: 'HAS_PART',
        right: {type: 'STRING_LIST', valueList: ['A', 'B', 'C']},
      },
    ],
    ['$.duties: Duties are currently unsupported.'],
  );
  export const failedMapping: UiPolicy = policy([], ['No constraints found!']);
}
