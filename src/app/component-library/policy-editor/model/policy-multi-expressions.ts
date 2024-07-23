import {UiPolicyExpressionType} from '@sovity.de/edc-client';

export interface PolicyMultiExpressionConfig {
  expressionType: UiPolicyExpressionType;
  title: string;
  description: string;
}

export const SUPPORTED_MULTI_EXPRESSIONS: PolicyMultiExpressionConfig[] = [
  {
    expressionType: 'AND',
    title: 'AND',
    description:
      'Conjunction of several expressions. Evaluates to true if and only if all child expressions are true',
  },
  {
    expressionType: 'OR',
    title: 'OR',
    description:
      'Disjunction of several expressions. Evaluates to true if and only if at least one child expression is true',
  },
  {
    expressionType: 'XONE',
    title: 'XONE',
    description:
      'XONE operation. Evaluates to true if and only if exactly one child expression is true',
  },
];
