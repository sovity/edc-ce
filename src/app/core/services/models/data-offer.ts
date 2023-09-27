import {UiDataOffer} from '@sovity.de/edc-client';
import {Asset} from './asset';
import {ContractOffer} from './contract-offer';

/**
 * Contract Offer (UI Dto)
 */
export type DataOffer = Omit<UiDataOffer, 'asset' | 'contractOffers'> & {
  asset: Asset;
  contractOffers: ContractOffer[];
};
