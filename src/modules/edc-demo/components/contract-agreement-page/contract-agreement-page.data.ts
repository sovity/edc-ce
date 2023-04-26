import {ContractAgreementPage} from '@sovity.de/edc-client';
import {ContractAgreementCardMapped} from '../contract-agreement-cards/contract-agreement-card-mapped';

export type ContractAgreementPageData = Omit<
  ContractAgreementPage,
  'contractAgreements'
> & {
  contractAgreements: ContractAgreementCardMapped[];
  numTotalContractAgreements: number;
};
