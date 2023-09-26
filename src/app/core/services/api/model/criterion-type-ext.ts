import {UiCriterionOperatorEnum} from '@sovity.de/edc-client';

export const CRITERION_OPERATOR_SYMBOLS: Record<
  UiCriterionOperatorEnum,
  string
> = {
  EQ: '=',
  IN: 'in',
  LIKE: 'like',
};
