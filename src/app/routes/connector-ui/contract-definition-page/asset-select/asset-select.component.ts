import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {AssetDetailDialogDataService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogComponent} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.component';
import {Asset} from '../../../../core/services/models/asset';

@Component({
  selector: 'asset-select',
  templateUrl: './asset-select.component.html',
})
export class AssetSelectComponent {
  @Input()
  label!: string;

  @Input()
  control!: FormControl<Asset[]>;

  @Input()
  assets: Asset[] = [];

  constructor(
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private matDialog: MatDialog,
  ) {}

  isEqualId(a: Asset | null, b: Asset | null): boolean {
    return a?.id === b?.id;
  }

  onAssetClick(asset: Asset) {
    const data = this.assetDetailDialogDataService.assetDetails(asset, false);
    this.matDialog.open(AssetDetailDialogComponent, {
      data,
      maxHeight: '90vh',
    });
  }
}
