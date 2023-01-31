import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {DataAddress} from '../../../edc-dmgmt-client';

@Component({
  selector: 'edc-demo-catalog-browser-transfer-dialog',
  templateUrl: './catalog-browser-transfer-dialog.component.html',
  styleUrls: ['./catalog-browser-transfer-dialog.component.scss'],
})
export class CatalogBrowserTransferDialog {
  name: string = '';
  dataDestination: string = '';
  type: string = 'Json';

  constructor(
    private dialogRef: MatDialogRef<CatalogBrowserTransferDialog>,
    @Inject(MAT_DIALOG_DATA) contractDefinition?: any,
  ) {}

  onTransfer() {
    if (this.type == 'Json') {
      const result = {
        dataDestination: JSON.parse(this.dataDestination.trim()),
      };
      this.dialogRef.close(result);
    } else if (this.type == 'Rest-Api') {
      const dataAddress: DataAddress = {
        properties: {
          type: 'HttpData',
          baseUrl: this.dataDestination.trim(),
        },
      };
      const result = {dataDestination: dataAddress};
      this.dialogRef.close(result);
    }
  }
}
