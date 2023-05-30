import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {AssetService} from './api/legacy-managent-api-client';
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
    private assetService: AssetService,
  ) {}

  fetchAssets(): Observable<Asset[]> {
    return this.assetService
      .getAllAssets(0, 10_000_000)
      .pipe(
        map((assets) =>
          assets.map((asset) =>
            this.assetPropertyMapper.buildAssetFromProperties(asset.properties),
          ),
        ),
      );
  }
}
