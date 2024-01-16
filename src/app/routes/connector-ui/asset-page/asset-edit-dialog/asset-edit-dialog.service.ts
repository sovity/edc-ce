import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {NEVER, Observable} from 'rxjs';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';
import {showDialogUntil} from '../../../../core/utils/mat-dialog-utils';
import {AssetEditDialogData} from './asset-edit-dialog-data';
import {AssetEditDialogResult} from './asset-edit-dialog-result';
import {AssetEditDialogComponent} from './asset-edit-dialog.component';
import {AssetEditDialogFormMapper} from './form/asset-edit-dialog-form-mapper';

@Injectable()
export class AssetEditDialogService {
  constructor(
    private dialog: MatDialog,
    private assetEditDialogFormMapper: AssetEditDialogFormMapper,
  ) {}

  showCreateDialog(
    until$: Observable<any> = NEVER,
  ): Observable<AssetEditDialogResult | undefined> {
    const initialFormValue = this.assetEditDialogFormMapper.forCreate();
    return this._open({initialFormValue}, until$);
  }

  showEditDialog(
    asset: UiAssetMapped,
    until$: Observable<any> = NEVER,
  ): Observable<AssetEditDialogResult | undefined> {
    const initialFormValue =
      this.assetEditDialogFormMapper.forEditMetadata(asset);
    return this._open({initialFormValue}, until$);
  }

  private _open(
    data: AssetEditDialogData,
    until$: Observable<any>,
  ): Observable<AssetEditDialogResult | undefined> {
    return showDialogUntil(
      this.dialog,
      AssetEditDialogComponent,
      {data},
      until$,
    );
  }
}
