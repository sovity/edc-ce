import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {AssetService} from '../../edc-dmgmt-client';
import {Asset} from '../models/asset';
import {AssetPropertyMapper} from './asset-property-mapper';

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
            this.assetPropertyMapper.readProperties(asset.properties),
          ),
        ),
      );
  }
}
