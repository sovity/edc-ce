import {Constraint} from './legacy-managent-api-client';

export const ExpressionLeftSideConstants = {
  PolicyEvaluationTime: 'POLICY_EVALUATION_TIME',
};

export const EdcTypes = {
  AtomicConstraint: 'AtomicConstraint',
  LiteralExpression: 'dataspaceconnector:literalexpression',
  Permission: 'dataspaceconnector:permission',
};

export const OperatorSymbols = {
  GT: '>',
  LT: '<',
  EQ: '=',
  NEQ: '≠',
  GEQ: '≥',
  LEQ: '≤',
  IN: '∈',
};

export type Operator = keyof typeof OperatorSymbols;

export interface AtomicConstraint extends Constraint {
  edctype: string;
  leftExpression: unknown;
  rightExpression: unknown;
  operator: Operator;
}

export interface LiteralExpression {
  edctype: string;
  value: string;
}
