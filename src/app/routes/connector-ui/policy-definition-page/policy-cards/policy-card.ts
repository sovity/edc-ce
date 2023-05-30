import {PolicyDefinition} from '../../../../core/services/api/legacy-managent-api-client';

export interface PolicyCard {
  id: string;
  isRegular: boolean;
  irregularities: string[];
  constraints: PolicyCardConstraint[];

  objectForJson: PolicyDefinition;
}

export interface PolicyCardConstraint {
  left: string;
  operator: string;
  right: string;
}
