import {
  ContractDefinitionDto,
  PolicyDefinition,
} from '../../../../core/services/api/legacy-managent-api-client';
import {Asset} from '../../../../core/services/models/asset';

export interface ContractDefinitionCard {
  id: string;
  criteria: ContractDefinitionCardCriterion[];
  contractPolicy: ContractDefinitionCardPolicy;
  accessPolicy: ContractDefinitionCardPolicy;

  detailJsonObj: ContractDefinitionDto;
}

export interface ContractDefinitionCardPolicy {
  policyDefinitionId: string;
  policyDefinition: PolicyDefinition | null;
}

export interface ContractDefinitionCardCriterion {
  label: string;
  values: ContractDefinitionCardCriterionValue[];
}

export interface ContractDefinitionCardCriterionValue {
  type: 'string' | 'asset' | 'json';
  searchTargets: (string | null)[];
  value?: string;
  asset?: Asset;
  json?: any;
}
