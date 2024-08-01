import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {BehaviorSubject, Subject} from 'rxjs';
import {filter, switchMap} from 'rxjs/operators';
import {OnAssetEditClickFn} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data';
import {AssetDetailDialogDataService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.service';
import {AssetService} from '../../../../core/services/asset.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';

export interface AssetList {
  filteredAssets: UiAssetMapped[];
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
    private assetServiceMapped: AssetService,
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private assetDetailDialogService: AssetDetailDialogService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.fetch$
      .pipe(
        switchMap(() => this.assetServiceMapped.fetchAssets()),
        Fetched.wrap({
          failureMessage: 'Failed fetching asset list.',
        }),
      )
      .pipe(
        Fetched.map(
          (assets): AssetList => ({
            filteredAssets: assets.filter((asset) =>
              asset.title
                ?.toLowerCase()
                .includes(this.searchText.toLowerCase()),
            ),
            numTotalAssets: assets.length,
          }),
        ),
      )
      .subscribe((assetList) => (this.assetList = assetList));
  }

  onSearch() {
    this.refresh();
  }

  onAssetClick(asset: UiAssetMapped) {
    const onAssetEditClick: OnAssetEditClickFn = (asset, onAssetUpdated) => {
      this.router.navigate(['/edit-asset', asset.assetId]);
    };

    const buildDialogData = (asset: UiAssetMapped) =>
      this.assetDetailDialogDataService.assetDetailsEditable(asset, {
        onAssetEditClick,
      });

    const data = buildDialogData(asset);
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
