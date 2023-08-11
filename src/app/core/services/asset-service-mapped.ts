import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
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
    private assetPropertyMapper: AssetPropertyMapper,
    private edcApiService: EdcApiService,
  ) {}

  fetchAssets(): Observable<Asset[]> {
    return this.edcApiService
      .getAssetPage()
      .pipe(
        map((assetPage) =>
          assetPage.assets.map((asset) =>
            this.assetPropertyMapper.buildAssetFromProperties(asset.properties),
          ),
        ),
      );
  }
}
