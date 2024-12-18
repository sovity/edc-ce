import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UrlListDialogData} from './url-list-dialog.data';

@Component({
  selector: 'app-json-dialog',
  templateUrl: './url-list-dialog.component.html',
})
export class UrlListDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<UrlListDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UrlListDialogData,
  ) {}
}
