import {Component, Input, OnDestroy} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Subject} from 'rxjs';
import {AssetDetailDialogDataService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.service';
import {Asset} from '../../../../core/services/models/asset';

@Component({
  selector: 'asset-select',
  templateUrl: './asset-select.component.html',
})
export class AssetSelectComponent implements OnDestroy {
  @Input()
  label!: string;

  @Input()
  control!: FormControl<Asset[]>;

  @Input()
  assets: Asset[] = [];

  constructor(
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private assetDetailDialogService: AssetDetailDialogService,
  ) {}

  onAssetClick(asset: Asset) {
    const data = this.assetDetailDialogDataService.assetDetails(asset, false);
    this.assetDetailDialogService.open(data, this.ngOnDestroy$);
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
