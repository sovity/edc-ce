import {ContractOfferDto} from "./contract-offer-dto";
import {Asset} from "./asset";

/**
 * Contract Offer (UI Dto)
 */
export type ContractOffer = Omit<ContractOfferDto, 'asset'> & {
  asset: Asset;
}
