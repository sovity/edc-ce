import {
  CatalogDataOffer,
  CatalogPageResult,
} from '@sovity.de/broker-server-client';
import {UiAssetMapped} from '../../../../../core/services/models/ui-asset-mapped';

/**
 * Broker Catalog Page, but with UiAssets replaced by UiAssetMapped
 */
export type CatalogPageResultMapped = Omit<CatalogPageResult, 'dataOffers'> & {
  dataOffers: CatalogDataOfferMapped[];
};

/**
 * Broker Catalog Data Offer, but with UiAssets replaced by UiAssetMapped
 */
export type CatalogDataOfferMapped = Omit<CatalogDataOffer, 'asset'> & {
  asset: UiAssetMapped;
};
