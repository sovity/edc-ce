import {Inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {APP_CONFIG, AppConfig} from '../config/app-config';
import {EdcApiService} from './api/edc-api.service';
import {AssetBuilder} from './asset-builder';
import {Asset} from './models/asset';

/**
 * Wrapped AssetService with AssetPropertyMapper
 */
@Injectable({
  providedIn: 'root',
})
export class AssetService {
  constructor(
    @Inject(APP_CONFIG) private config: AppConfig,
    private assetBuilder: AssetBuilder,
    private edcApiService: EdcApiService,
  ) {}

  fetchAssets(): Observable<Asset[]> {
    return this.edcApiService
      .getAssetPage()
      .pipe(
        map((assetPage) =>
          assetPage.assets.map((asset) =>
            this.assetBuilder.buildAsset(asset, this.config.connectorEndpoint),
          ),
        ),
      );
  }
}
