import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import {Subject} from 'rxjs';
import {filter, finalize, takeUntil} from 'rxjs/operators';
import cleanDeep from 'clean-deep';
import {ConfirmationDialogComponent} from '../confirmation-dialog/confirmation-dialog.component';
import {DialogButton, JsonDialogData} from './json-dialog.data';

@Component({
  selector: 'app-json-dialog',
  templateUrl: './json-dialog.component.html',
})
export class JsonDialogComponent implements OnInit, OnDestroy {
  busy = false;

  removeNulls = true;

  visibleJson: unknown = {};

  constructor(
    public dialogRef: MatDialogRef<JsonDialogComponent>,
    private matDialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: JsonDialogData,
  ) {}

  ngOnInit() {
    this.updateVisibleJson();
  }

  updateVisibleJson() {
    this.visibleJson = this.removeNulls
      ? cleanDeep(this.data.objectForJson, {emptyStrings: false})
      : this.data.objectForJson;
  }

  onAction(button: DialogButton) {
    if (button.confirmation) {
      const ref = this.matDialog.open(ConfirmationDialogComponent, {
        maxWidth: '20%',
        data: button.confirmation,
      });

      ref
        .afterClosed()
        .pipe(filter((it) => it))
        .subscribe(() => this.doAction(button));
    } else {
      this.doAction(button);
    }
  }

  doAction(button: DialogButton) {
    if (this.busy) {
      return;
    }
    this.busy = true;
    button
      .action()
      .pipe(
        finalize(() => (this.busy = false)),
        takeUntil(this.ngOnDestroy$),
      )
      .subscribe();
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
