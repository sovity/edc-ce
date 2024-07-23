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
    evaluationTimeConstraint('LT', addDays(lastDay, 1)),
  );
};
