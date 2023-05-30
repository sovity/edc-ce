import {ContractOfferDto} from './contract-offer-dto';

/**
 * Contract Offer List (API Model)
 */
export interface ContractOfferResponseDto {
  id: string;
  contractOffers: ContractOfferDto[];
}
