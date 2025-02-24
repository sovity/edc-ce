import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ConditionsForUseDialogData} from './conditions-for-use-dialog.data';

@Component({
  selector: 'conditions-for-use-dialog',
  templateUrl: './conditions-for-use-dialog.component.html',
})
export class ConditionsForUseDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConditionsForUseDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConditionsForUseDialogData,
  ) {}
}
