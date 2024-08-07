import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {NEVER, Observable} from 'rxjs';
import {showDialogUntil} from '../../../core/utils/mat-dialog-utils';
import {ConditionsForUseDialogComponent} from './conditions-for-use-dialog.component';
import {ConditionsForUseDialogData} from './conditions-for-use-dialog.data';

@Injectable()
export class ConditionsForUseDialogService {
  constructor(private dialog: MatDialog) {}

  /**
   * Shows JSON Detail Dialog until until$ emits / completes
   * @param data json detail dialog data
   * @param until$ observable that controls the lifetime of the dialog
   */
  showConditionsForUseDialog(
    data: ConditionsForUseDialogData,
    until$: Observable<any> = NEVER,
  ): Observable<unknown> {
    return showDialogUntil(
      this.dialog,
      ConditionsForUseDialogComponent,
      {data, autoFocus: false},
      until$,
    );
  }
}
