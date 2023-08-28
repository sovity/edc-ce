import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject, Subject} from 'rxjs';
import {filter, map, switchMap} from 'rxjs/operators';
import {AssetDetailDialogDataService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.service';
import {AssetServiceMapped} from '../../../../core/services/asset-service-mapped';
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
export class AssetPageComponent implements OnInit, OnDestroy {
  assetList: Fetched<AssetList> = Fetched.empty();
  searchText = '';
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private assetServiceMapped: AssetServiceMapped,
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private assetDetailDialogService: AssetDetailDialogService,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.fetch$
      .pipe(
        switchMap(() => {
          return this.assetServiceMapped.fetchAssets().pipe(
            map(
              (assets): AssetList => ({
                filteredAssets: assets.filter((asset) =>
                  asset.name?.includes(this.searchText),
                ),
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
    const data = this.assetDetailDialogDataService.assetDetails(asset, true);
    this.assetDetailDialogService
      .open(data, this.ngOnDestroy$)
      .pipe(filter((it) => !!it?.refreshList))
      .subscribe(() => this.refresh());
  }

  private refresh() {
    this.fetch$.next(null);
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
