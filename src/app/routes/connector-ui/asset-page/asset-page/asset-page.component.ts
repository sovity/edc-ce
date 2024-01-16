import {Component, OnDestroy, OnInit} from '@angular/core';
import {BehaviorSubject, Subject, merge} from 'rxjs';
import {filter, map, switchMap} from 'rxjs/operators';
import {OnAssetEditClickFn} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data';
import {AssetDetailDialogDataService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.service';
import {AssetService} from '../../../../core/services/asset.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';
import {filterNotNull} from '../../../../core/utils/rxjs-utils';
import {AssetEditDialogService} from '../asset-edit-dialog/asset-edit-dialog.service';

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
  assetListUpdater$ = new Subject<UiAssetMapped[]>();
  searchText = '';
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private assetServiceMapped: AssetService,
    private assetEditDialogService: AssetEditDialogService,
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private assetDetailDialogService: AssetDetailDialogService,
  ) {}

  ngOnInit(): void {
    merge(
      this.fetch$.pipe(
        switchMap(() => this.assetServiceMapped.fetchAssets()),
        Fetched.wrap({
          failureMessage: 'Failed fetching asset list.',
        }),
      ),
      this.assetListUpdater$.pipe(map(Fetched.ready)),
    )
      .pipe(
        Fetched.map(
          (assets): AssetList => ({
            filteredAssets: assets.filter((asset) =>
              asset.title?.includes(this.searchText),
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

  onCreate() {
    this.assetEditDialogService
      .showCreateDialog(this.ngOnDestroy$)
      .subscribe((result) => {
        if (result?.refreshedList) {
          this.assetListUpdater$.next(result.refreshedList);
        }
      });
  }

  onAssetClick(asset: UiAssetMapped) {
    const onAssetEditClick: OnAssetEditClickFn = (asset, onAssetUpdated) => {
      this.assetEditDialogService
        .showEditDialog(asset, this.ngOnDestroy$)
        .pipe(filterNotNull())
        .subscribe((result) => {
          this.assetListUpdater$.next(result.refreshedList);
          onAssetUpdated(buildDialogData(result.asset));
        });
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
