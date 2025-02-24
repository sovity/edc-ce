import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {BehaviorSubject, Subject} from 'rxjs';
import {filter, switchMap} from 'rxjs/operators';
import {AssetService} from '../../../../core/services/asset.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';
import {OnAssetEditClickFn} from '../../../../shared/business/asset-detail-dialog/asset-detail-dialog-data';
import {AssetDetailDialogDataService} from '../../../../shared/business/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogService} from '../../../../shared/business/asset-detail-dialog/asset-detail-dialog.service';
import {AssetCreateDialogService} from '../asset-create-dialog/asset-create-dialog.service';

export interface AssetList {
  filteredAssets: UiAssetMapped[];
  numTotalAssets: number;
}

@Component({
  selector: 'asset-list-page',
  templateUrl: './asset-list-page.component.html',
  styleUrls: ['./asset-list-page.component.scss'],
})
export class AssetListPageComponent implements OnInit, OnDestroy {
  assetList: Fetched<AssetList> = Fetched.empty();
  searchText = '';
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private assetServiceMapped: AssetService,
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private assetDetailDialogService: AssetDetailDialogService,
    private assetCreateDialogService: AssetCreateDialogService,
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
    const onAssetEditClick: OnAssetEditClickFn = (asset) => {
      this.router.navigate(['/my-assets', asset.assetId, 'edit']);
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

  onCreate() {
    this.assetCreateDialogService
      .showCreateDialog(this.ngOnDestroy$)
      .subscribe((result) => {
        if (result?.refreshedList) {
          this.refresh();
        }
      });
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
