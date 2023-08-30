import {
  ContractDefinitionEntry,
  PolicyDefinitionDto,
} from '@sovity.de/edc-client';
import {Asset} from '../../../../core/services/models/asset';

export interface ContractDefinitionCard {
  id: string;
  criteria: ContractDefinitionCardCriterion[];
  contractPolicy: ContractDefinitionCardPolicy;
  accessPolicy: ContractDefinitionCardPolicy;

  detailJsonObj: ContractDefinitionEntry;
}

export interface ContractDefinitionCardPolicy {
  policyDefinitionId: string;
  policyDefinition: PolicyDefinitionDto | null;
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
