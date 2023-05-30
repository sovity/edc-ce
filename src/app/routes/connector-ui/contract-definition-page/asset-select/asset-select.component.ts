import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {AssetDetailDialogData} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data';
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

  constructor(private matDialog: MatDialog) {}

  isEqualId(a: Asset | null, b: Asset | null): boolean {
    return a?.id === b?.id;
  }

  onAssetClick(asset: Asset) {
    const data = AssetDetailDialogData.forAssetDetails(asset, false);
    this.matDialog.open(AssetDetailDialogComponent, {
      data,
      maxHeight: '90vh',
    });
  }
}
