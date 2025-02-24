import {PolicyMultiExpressionConfig} from '../model/policy-multi-expressions';
import {PolicyOperatorConfig} from '../model/policy-operators';
import {PolicyVerbConfig} from '../model/policy-verbs';

export interface ExpressionFormValue {
  type: 'CONSTRAINT' | 'MULTI' | 'EMPTY';

  multiExpression?: PolicyMultiExpressionConfig;
  verb?: PolicyVerbConfig;
  supportedOperators?: PolicyOperatorConfig[];
}
