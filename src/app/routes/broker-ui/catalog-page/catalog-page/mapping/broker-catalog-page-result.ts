import {CatalogPageResult} from '@sovity.de/broker-server-client';
import {BrokerDataOffer} from './broker-data-offer';

/**
 * Catalog Page, but with Assets replaced with type safe assets
 */
export type BrokerCatalogPageResult = Omit<CatalogPageResult, 'dataOffers'> & {
  dataOffers: BrokerDataOffer[];
};
