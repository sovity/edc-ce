import {Component} from '@angular/core';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-initiate-transfer-confirm-tos-dialog',
  templateUrl: './initiate-transfer-confirm-tos-dialog.component.html',
  styleUrls: ['./initiate-transfer-confirm-tos-dialog.component.scss'],
})
export class InitiateTransferConfirmTosDialogComponent {
  checkboxChecked = false;

  constructor(
    public dialogRef: MatDialogRef<InitiateTransferConfirmTosDialogComponent>,
  ) {}

  public onCheckboxChange($event: MatCheckboxChange) {
    this.checkboxChecked = $event.checked;
  }

  onCancel() {
    this.dialogRef.close(false);
  }

  onConfirm() {
    if (this.checkboxChecked) {
      this.dialogRef.close(true);
    }
  }
}
