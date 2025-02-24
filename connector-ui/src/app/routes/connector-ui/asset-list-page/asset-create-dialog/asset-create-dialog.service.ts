import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {NEVER, Observable} from 'rxjs';
import {showDialogUntil} from '../../../../core/utils/mat-dialog-utils';
import {AssetCreateDialogData} from './asset-create-dialog-data';
import {AssetCreateDialogResult} from './asset-create-dialog-result';
import {AssetCreateDialogComponent} from './asset-create-dialog.component';
import {AssetCreateDialogFormMapper} from './form/asset-create-dialog-form-mapper';

@Injectable()
export class AssetCreateDialogService {
  constructor(
    private dialog: MatDialog,
    private assetCreateDialogFormMapper: AssetCreateDialogFormMapper,
  ) {}

  showCreateDialog(
    until$: Observable<any> = NEVER,
  ): Observable<AssetCreateDialogResult | undefined> {
    const initialFormValue = this.assetCreateDialogFormMapper.forCreate();
    return this._open({initialFormValue}, until$);
  }

  private _open(
    data: AssetCreateDialogData,
    until$: Observable<any>,
  ): Observable<AssetCreateDialogResult | undefined> {
    return showDialogUntil(
      this.dialog,
      AssetCreateDialogComponent,
      {data},
      until$,
    );
  }
}
