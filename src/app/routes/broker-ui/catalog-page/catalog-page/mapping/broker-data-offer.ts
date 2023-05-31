import {DataOfferListEntry} from '@sovity.de/edc-client';
import {Asset} from '../../../../../core/services/models/asset';

/**
 * Contract Offer, but with Assets replaced with type safe assets
 */
export type BrokerDataOffer = DataOfferListEntry & {
  asset: Asset;
};
