import {ContractDefinitionCard} from '../contract-definition-cards/contract-definition-card';

export interface ContractDefinitionPageData {
  contractDefinitionCards: ContractDefinitionCard[];
  numTotalContractDefinitions: number;
}
