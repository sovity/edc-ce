import {Injectable} from '@angular/core';
import {
  CatalogDataOffer,
  CatalogPageResult,
} from '@sovity.de/broker-server-client';
import {AssetBuilder} from '../../../../../core/services/asset-builder';
import {BrokerCatalogPageResult} from './broker-catalog-page-result';
import {BrokerDataOffer} from './broker-data-offer';

@Injectable({providedIn: 'root'})
export class BrokerCatalogMapper {
  constructor(private assetBuilder: AssetBuilder) {}

  buildUiCatalogPageResult(data: CatalogPageResult): BrokerCatalogPageResult {
    return {
      ...data,
      dataOffers: data.dataOffers.map((offer) => this.buildUiDataOffer(offer)),
    };
  }

  private buildUiDataOffer(offer: CatalogDataOffer): BrokerDataOffer {
    return {
      ...offer,
      asset: this.assetBuilder.buildAsset(
        {
          assetId: offer.properties['asset:prop:id'],
          name: offer.properties['asset:prop:id'],
        },
        offer.connectorEndpoint,
      ),
    };
  }
}
