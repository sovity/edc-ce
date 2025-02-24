import {UiDataOffer} from '@sovity.de/edc-client';
import {ContractOffer} from './contract-offer';
import {UiAssetMapped} from './ui-asset-mapped';

/**
 * Contract Offer (UI Dto)
 */
export type DataOffer = Omit<UiDataOffer, 'asset' | 'contractOffers'> & {
  asset: UiAssetMapped;
  contractOffers: ContractOffer[];
};
