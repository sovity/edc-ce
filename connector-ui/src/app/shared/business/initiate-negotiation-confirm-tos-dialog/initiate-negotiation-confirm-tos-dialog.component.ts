import {Component} from '@angular/core';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-initiate-negotiation-confirm-tos-dialog',
  templateUrl: './initiate-negotiation-confirm-tos-dialog.component.html',
  styleUrls: ['./initiate-negotiation-confirm-tos-dialog.component.scss'],
})
export class InitiateNegotiationConfirmTosDialogComponent {
  checkboxChecked = false;

  constructor(
    public dialogRef: MatDialogRef<InitiateNegotiationConfirmTosDialogComponent>,
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
