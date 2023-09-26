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
  IN: '∈',
  HAS_PART: 'HAS PART',
  IS_A: 'IS A',
  IS_ALL_OF: 'IS ALL OF',
  IS_ANY_OF: 'IS ANY OF',
  IS_NONE_OF: 'IS NONE OF',
};
