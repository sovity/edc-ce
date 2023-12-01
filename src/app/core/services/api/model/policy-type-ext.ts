import {OperatorDto} from '@sovity.de/edc-client';

export const PolicyLeftExpressions = {
  PolicyEvaluationTime: 'POLICY_EVALUATION_TIME',
  ReferringConnector: 'REFERRING_CONNECTOR',
};

export const OPERATOR_SYMBOLS: Record<OperatorDto, string> = {
  GT: '>',
  LT: '<',
  EQ: '=',
  NEQ: '≠',
  GEQ: '≥',
  LEQ: '≤',
  IN: 'IN',
  HAS_PART: '`HAS_PART`',
  IS_A: '`IS_A`',
  IS_ALL_OF: '`IS_ALL_OF`',
  IS_ANY_OF: '`IS_ANY_OF`',
  IS_NONE_OF: '`IS_NONE_OF`',
};
