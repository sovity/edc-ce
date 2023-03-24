import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {AssetService} from '../../../edc-dmgmt-client';
import {Asset} from '../../models/asset';
import {Fetched} from '../../models/fetched';
import {AssetPropertyMapper} from '../../services/asset-property-mapper';
import {AssetDetailDialogData} from '../asset-detail-dialog/asset-detail-dialog-data';
import {AssetDetailDialogResult} from '../asset-detail-dialog/asset-detail-dialog-result';
import {AssetDetailDialog} from '../asset-detail-dialog/asset-detail-dialog.component';
import {AssetEditorDialogResult} from '../asset-editor-dialog/asset-editor-dialog-result';
import {AssetEditorDialog} from '../asset-editor-dialog/asset-editor-dialog.component';

export interface AssetList {
  filteredAssets: Asset[];
  numTotalAssets: number;
}

@Component({
  selector: 'edc-demo-asset-viewer',
  templateUrl: './asset-viewer.component.html',
  styleUrls: ['./asset-viewer.component.scss'],
})
export class AssetViewerComponent implements OnInit {
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
                    this.assetPropertyMapper.readProperties(asset.properties),
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
    const ref = this.dialog.open(AssetEditorDialog);
    ref.afterClosed().subscribe((result: AssetEditorDialogResult) => {
      if (result?.refreshList) {
        this.refresh();
      }
    });
  }

  onAssetClick(asset: Asset) {
    const data = AssetDetailDialogData.forAssetDetails(asset, true);
    const ref = this.dialog.open(AssetDetailDialog, {
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
