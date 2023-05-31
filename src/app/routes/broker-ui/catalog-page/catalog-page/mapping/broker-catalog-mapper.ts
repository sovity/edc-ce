import {Injectable} from '@angular/core';
import {CatalogPageResult, DataOfferListEntry} from '@sovity.de/edc-client';
import {AssetPropertyMapper} from '../../../../../core/services/asset-property-mapper';
import {BrokerCatalogPageResult} from './broker-catalog-page-result';
import {BrokerDataOffer} from './broker-data-offer';

@Injectable({providedIn: 'root'})
export class BrokerCatalogMapper {
  constructor(private assetPropertyMapper: AssetPropertyMapper) {}

  buildUiCatalogPageResult(data: CatalogPageResult): BrokerCatalogPageResult {
    return {
      ...data,
      dataOffers: data.dataOffers.map((offer) => this.buildUiDataOffer(offer)),
    };
  }

  private buildUiDataOffer(offer: DataOfferListEntry): BrokerDataOffer {
    return {
      ...offer,
      asset: this.assetPropertyMapper.buildAssetFromProperties(
        offer.properties,
      ),
    };
  }
}
