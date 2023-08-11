import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {AssetDetailDialogDataService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogResult} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-result';
import {AssetDetailDialogComponent} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.component';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
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
    private edcApiService: EdcApiService,
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private dialog: MatDialog,
    private assetPropertyMapper: AssetPropertyMapper,
  ) {}

  ngOnInit(): void {
    this.fetch$
      .pipe(
        switchMap(() => {
          return this.edcApiService.getAssetPage().pipe(
            map(
              (assetPage): AssetList => ({
                filteredAssets: assetPage.assets
                  .map((asset) =>
                    this.assetPropertyMapper.buildAssetFromProperties(
                      asset.properties,
                    ),
                  )
                  .filter((asset) => asset.name?.includes(this.searchText)),
                numTotalAssets: assetPage.assets.length,
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
    const data = this.assetDetailDialogDataService.assetDetails(asset, true);
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
