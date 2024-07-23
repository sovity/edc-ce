import {OperatorDto} from '@sovity.de/edc-client';
import {
  PolicyFormAdapter,
  jsonAdapter,
  localDateAdapter,
  stringArrayOrCommaJoinedAdapter,
} from './policy-form-adapter';
import {policyLeftExpressions} from './policy-left-expressions';
import {SUPPORTED_POLICY_OPERATORS} from './policy-operators';

export interface PolicyVerbConfig {
  operandLeftId: string;
  operandLeftTitle: string;
  operandLeftDescription: string;
  operandRightType: 'DATE' | 'TEXT' | 'PARTICIPANT_ID';
  operandRightHint?: string;
  operandRightPlaceholder?: string;
  supportedOperators: OperatorDto[];
  adapter: PolicyFormAdapter<any>;
}

export const SUPPORTED_POLICY_VERBS: PolicyVerbConfig[] = [
  {
    operandLeftId: policyLeftExpressions.policyEvaluationTime,
    operandLeftTitle: 'Evaluation Time',
    operandLeftDescription:
      'Time at which the policy is evaluated. This can be used to restrict the data offer to certain time periods',
    supportedOperators: ['GEQ', 'LEQ', 'GT', 'LT'],
    operandRightType: 'DATE',
    operandRightPlaceholder: 'MM/DD/YYYY',
    operandRightHint: 'MM/DD/YYYY',
    adapter: localDateAdapter,
  },
  {
    operandLeftId: policyLeftExpressions.referringConnector,
    operandLeftTitle: 'Participant ID',
    operandLeftDescription:
      'Participant ID, also called Connector ID, of the counter-party connector.',
    operandRightType: 'PARTICIPANT_ID',
    supportedOperators: ['EQ', 'IN'],
    operandRightPlaceholder: 'MDSL1234XX.C1234YY',
    operandRightHint: 'Multiple values can be joined by comma',
    adapter: stringArrayOrCommaJoinedAdapter,
  },
];

export const defaultPolicyVerbConfig = (verb: string): PolicyVerbConfig => ({
  operandLeftId: verb,
  operandLeftTitle: verb,
  operandLeftDescription: '',
  supportedOperators: SUPPORTED_POLICY_OPERATORS.map((it) => it.id),
  operandRightType: 'TEXT',
  adapter: jsonAdapter,
});
