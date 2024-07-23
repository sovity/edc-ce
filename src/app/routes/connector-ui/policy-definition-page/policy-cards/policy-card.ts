import {PolicyExpressionMapped} from '../../../../component-library/policy-editor/model/policy-expression-mapped';

export interface PolicyCard {
  id: string;
  isRegular: boolean;
  irregularities: string[];
  expression: PolicyExpressionMapped;

  searchText: string;

  objectForJson: any;
}

export interface PolicyCardConstraint {
  left: string;
  operator: string;
  right: string;
}
