import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {AssetDetailDialogData} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data';
import {AssetDetailDialogResult} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-result';
import {AssetDetailDialogComponent} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.component';
import {AssetService} from '../../../../core/services/api/legacy-managent-api-client';
import {AssetPropertyMapper} from '../../../../core/services/asset-property-mapper';
import {Asset} from '../../../../core/services/models/asset';
import {Fetched} from '../../../../core/services/models/fetched';
import {AssetCreateDialogResult} from '../asset-create-dialog/asset-create-dialog-result';
import {AssetCreateDialogComponent} from '../asset-create-dialog/asset-create-dialog.component';

export interface AssetList {
  filteredAssets: Asset[];
  numTotalAssets: number;
}

@Component({
  selector: 'asset-page',
  templateUrl: './asset-page.component.html',
  styleUrls: ['./asset-page.component.scss'],
})
export class AssetPageComponent implements OnInit {
  assetList: Fetched<AssetList> = Fetched.empty();
  searchText = '';
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private assetService: AssetService,
    private dialog: MatDialog,
    private assetPropertyMapper: AssetPropertyMapper,
  ) {}

  ngOnInit(): void {
    this.fetch$
      .pipe(
        switchMap(() => {
          return this.assetService.getAllAssets(0, 10_000_000).pipe(
            map(
              (assets): AssetList => ({
                filteredAssets: assets
                  .map((asset) =>
                    this.assetPropertyMapper.buildAssetFromProperties(
                      asset.properties,
                    ),
                  )
                  .filter((asset) => asset.name?.includes(this.searchText)),
                numTotalAssets: assets.length,
              }),
            ),
            Fetched.wrap({
              failureMessage: 'Failed fetching asset list.',
            }),
          );
        }),
      )
      .subscribe((assetList) => (this.assetList = assetList));
  }

  onSearch() {
    this.refresh();
  }

  onCreate() {
    const ref = this.dialog.open(AssetCreateDialogComponent);
    ref.afterClosed().subscribe((result: AssetCreateDialogResult) => {
      if (result?.refreshList) {
        this.refresh();
      }
    });
  }

  onAssetClick(asset: Asset) {
    const data = AssetDetailDialogData.forAssetDetails(asset, true);
    const ref = this.dialog.open(AssetDetailDialogComponent, {
      data,
      maxHeight: '90vh',
    });
    ref.afterClosed().subscribe((result: AssetDetailDialogResult) => {
      if (result?.refreshList) {
        this.refresh();
      }
    });
  }

  private refresh() {
    this.fetch$.next(null);
  }
}
