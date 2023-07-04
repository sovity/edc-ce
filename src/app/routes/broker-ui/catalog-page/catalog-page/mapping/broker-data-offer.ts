import {CatalogDataOffer} from '@sovity.de/broker-server-client';
import {Asset} from '../../../../../core/services/models/asset';

/**
 * Contract Offer, but with Assets replaced with type safe assets
 */
export type BrokerDataOffer = CatalogDataOffer & {
  asset: Asset;
};
