/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {OperatorDto, UiPolicyExpression} from '@sovity.de/edc-client';
import {addDays} from 'date-fns';
import {policyLeftExpressions} from '../../../model/policy-left-expressions';
import {constraint, multi} from '../../../model/ui-policy-expression-utils';

export const buildTimespanRestriction = (
  firstDay: Date,
  lastDay: Date,
): UiPolicyExpression => {
  const evaluationTimeConstraint = (operator: OperatorDto, value: Date) =>
    constraint(
      policyLeftExpressions.policyEvaluationTime,
      operator,
      value.toISOString(),
    );

  return multi(
    'AND',
    evaluationTimeConstraint('GEQ', firstDay),
    evaluationTimeConstraint('LEQ', addDays(lastDay, 1)),
  );
};
