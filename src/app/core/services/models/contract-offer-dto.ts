import {UiAsset} from '@sovity.de/edc-client';
import {Policy} from '../api/legacy-managent-api-client';

/**
 * Contract Offer (API Model)
 */
export interface ContractOfferDto {
  id: string;
  policy: Policy;
  provider: string;
  consumer: string;
  asset: UiAsset;
}
