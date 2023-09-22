import {Inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {APP_CONFIG, AppConfig} from '../config/app-config';
import {EdcApiService} from './api/edc-api.service';
import {AssetPropertyMapper} from './asset-property-mapper';
import {Asset} from './models/asset';

/**
 * Wrapped AssetService with AssetPropertyMapper
 */
@Injectable({
  providedIn: 'root',
})
export class AssetServiceMapped {
  constructor(
    @Inject(APP_CONFIG) private config: AppConfig,
    private assetPropertyMapper: AssetPropertyMapper,
    private edcApiService: EdcApiService,
  ) {}

  fetchAssets(): Observable<Asset[]> {
    return this.edcApiService.getAssetPage().pipe(
      map((assetPage) =>
        assetPage.assets.map((uiAsset) =>
          this.assetPropertyMapper.buildAsset({
            connectorEndpoint: this.config.connectorEndpoint,
            uiAsset,
          }),
        ),
      ),
    );
  }
}
