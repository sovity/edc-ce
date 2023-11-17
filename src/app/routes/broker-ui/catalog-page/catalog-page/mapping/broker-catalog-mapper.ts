import {Injectable} from '@angular/core';
import {CatalogPageResult} from '@sovity.de/broker-server-client';
import {AssetBuilder} from '../../../../../core/services/asset-builder';
import {CatalogPageResultMapped} from './catalog-page-result-mapped';

@Injectable({providedIn: 'root'})
export class BrokerCatalogMapper {
  constructor(private assetBuilder: AssetBuilder) {}

  buildCatalogPageResultMapped(
    data: CatalogPageResult,
  ): CatalogPageResultMapped {
    return {
      ...data,
      dataOffers: data.dataOffers.map((offer) => ({
        ...offer,
        asset: this.assetBuilder.buildAsset(offer.asset),
      })),
    };
  }
}
