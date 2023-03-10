import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {Asset} from '../../models/asset';
import {AssetDetailDialogData} from '../asset-detail-dialog/asset-detail-dialog-data';
import {AssetDetailDialog} from '../asset-detail-dialog/asset-detail-dialog.component';

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
    this.matDialog.open(AssetDetailDialog, {
      data,
      maxHeight: '90vh',
    });
  }
}
