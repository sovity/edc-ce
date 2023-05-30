import {Asset} from './asset';
import {ContractOfferDto} from './contract-offer-dto';

/**
 * Contract Offer (UI Dto)
 */
export type ContractOffer = Omit<ContractOfferDto, 'asset'> & {
  asset: Asset;
};
