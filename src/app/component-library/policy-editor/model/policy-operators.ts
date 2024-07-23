import {OperatorDto} from '@sovity.de/edc-client';

export interface PolicyOperatorConfig {
  id: OperatorDto;
  title: string;
  description: string;
}

export const SUPPORTED_POLICY_OPERATORS: PolicyOperatorConfig[] = [
  {
    id: 'EQ',
    title: '=',
    description: 'Equal to',
  },
  {
    id: 'NEQ',
    title: '≠',
    description: 'Not equal to',
  },
  {
    id: 'GEQ',
    title: '≥',
    description: 'Greater than or equal to',
  },
  {
    id: 'GT',
    title: '>',
    description: 'Greater than',
  },
  {
    id: 'LEQ',
    title: '≤',
    description: 'Less than or equal to',
  },
  {
    id: 'LT',
    title: '<',
    description: 'Less than',
  },
  {
    id: 'IN',
    title: 'IN',
    description: 'In',
  },
  {
    id: 'HAS_PART',
    title: 'HAS PART',
    description: 'Has Part',
  },
  {
    id: 'IS_A',
    title: 'IS A',
    description: 'Is a',
  },
  {
    id: 'IS_NONE_OF',
    title: 'IS NONE OF',
    description: 'Is none of',
  },
  {
    id: 'IS_ANY_OF',
    title: 'IS ANY OF',
    description: 'Is any of',
  },
  {
    id: 'IS_ALL_OF',
    title: 'IS ALL OF',
    description: 'Is all of',
  },
];
export const defaultPolicyOperatorConfig = (
  operator: OperatorDto,
): PolicyOperatorConfig => ({
  id: operator,
  title: operator,
  description: '',
});
