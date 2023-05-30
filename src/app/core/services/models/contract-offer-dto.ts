import {Policy} from '../api/legacy-managent-api-client';
import {AssetDto} from './asset-dto';

/**
 * Contract Offer (API Model)
 */
export interface ContractOfferDto {
  id: string;
  policy: Policy;
  provider: string;
  consumer: string;
  asset: AssetDto;
}
