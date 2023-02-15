import {PolicyDefinition} from '../../../edc-dmgmt-client';

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
