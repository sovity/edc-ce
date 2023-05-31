import {Fetched} from '../../../../core/services/models/fetched';
import {BrokerCatalogPageResult} from './mapping/broker-catalog-page-result';

export interface CatalogPageData {
  fetchedDataOffers: Fetched<BrokerCatalogPageResult>;
}

export function emptyCatalogPageStateModel(): CatalogPageData {
  return {
    fetchedDataOffers: Fetched.empty(),
  };
}
