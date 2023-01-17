import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {AssetService} from '../../../edc-dmgmt-client';
import {Asset} from '../../models/asset';
import {AssetPropertyMapper} from '../../services/asset-property-mapper';
import {AssetDetailDialogData} from '../asset-detail-dialog/asset-detail-dialog-data';
import {AssetDetailDialogResult} from '../asset-detail-dialog/asset-detail-dialog-result';
import {AssetDetailDialog} from '../asset-detail-dialog/asset-detail-dialog.component';
import {AssetEditorDialogResult} from '../asset-editor-dialog/asset-editor-dialog-result';
import {AssetEditorDialog} from '../asset-editor-dialog/asset-editor-dialog.component';

@Component({
  selector: 'edc-demo-asset-viewer',
  templateUrl: './asset-viewer.component.html',
  styleUrls: ['./asset-viewer.component.scss'],
})
export class AssetViewerComponent implements OnInit {
  filteredAssets$: Observable<Asset[]> = of([]);
  searchText = '';
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private assetService: AssetService,
    private dialog: MatDialog,
    private assetPropertyMapper: AssetPropertyMapper,
  ) {}

  ngOnInit(): void {
    this.filteredAssets$ = this.fetch$.pipe(
      switchMap(() => {
        let assets$ = this.assetService
          .getAllAssets()
          .pipe(
            map((assets) =>
              assets.map((asset) =>
                this.assetPropertyMapper.readProperties(asset.properties),
              ),
            ),
          );

        if (this.searchText) {
          assets$ = assets$.pipe(
            map((assets) =>
              assets.filter((asset) => asset.name?.includes(this.searchText)),
            ),
          );
        }

        return assets$;
      }),
    );
  }

  onSearch() {
    this.refresh();
  }

  onCreate() {
    const ref = this.dialog.open(AssetEditorDialog);
    ref.afterClosed().subscribe((result: AssetEditorDialogResult) => {
      if (result.refreshList) {
        this.refresh();
      }
    });
  }

  onAssetClick(asset: Asset) {
    const data = AssetDetailDialogData.forAssetDetails(asset);
    const ref = this.dialog.open(AssetDetailDialog, {
      data,
      maxHeight: '90vh',
    });
    ref.afterClosed().subscribe((result: AssetDetailDialogResult) => {
      if (result.refreshList) {
        this.refresh();
      }
    });
  }

  private refresh() {
    this.fetch$.next(null);
  }
}
