import {DataOffer} from '@sovity.de/edc-client';
import {Asset} from '../../../../../core/services/models/asset';

/**
 * Contract Offer, but with Assets replaced with type safe assets
 */
export type BrokerDataOffer = Omit<DataOffer, 'asset'> & {asset: Asset};
