/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  type OperatorDto,
  type UiPolicyExpression,
  type UiPolicyExpressionType,
} from '@sovity.de/edc-client';

export const constraint = (
  left: string,
  operator: OperatorDto,
  value: string,
): UiPolicyExpression => ({
  type: 'CONSTRAINT',
  constraint: {
    left,
    operator,
    right: {type: 'STRING', value},
  },
});

export const constraintList = (
  left: string,
  operator: OperatorDto,
  valueList: string[],
): UiPolicyExpression => ({
  type: 'CONSTRAINT',
  constraint: {
    left,
    operator,
    right: {type: 'STRING_LIST', valueList},
  },
});

export const multi = (
  type: Exclude<UiPolicyExpressionType, 'EMPTY' | 'CONSTRAINT'>,
  ...expressions: UiPolicyExpression[]
): UiPolicyExpression => ({
  type,
  expressions,
});
