/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {EdcApiService} from './api/edc-api.service';
import {AssetBuilder} from './asset-builder';
import {UiAssetMapped} from './models/ui-asset-mapped';

/**
 * Wrapped AssetService with AssetPropertyMapper
 */
@Injectable({
  providedIn: 'root',
})
export class AssetService {
  constructor(
    private assetBuilder: AssetBuilder,
    private edcApiService: EdcApiService,
  ) {}

  fetchAssets(): Observable<UiAssetMapped[]> {
    return this.edcApiService
      .getAssetPage()
      .pipe(
        map((assetPage) =>
          assetPage.assets.map((asset) => this.assetBuilder.buildAsset(asset)),
        ),
      );
  }
}
