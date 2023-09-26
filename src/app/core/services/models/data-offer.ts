import {UiDataOffer} from '@sovity.de/edc-client';
import {Asset} from './asset';

/**
 * Contract Offer (UI Dto)
 */
export type DataOffer = Omit<UiDataOffer, 'asset'> & {
  asset: Asset;
};
