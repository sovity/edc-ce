import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {StorageType} from '../../models/storage-type';


@Component({
  selector: 'edc-demo-catalog-browser-transfer-dialog',
  templateUrl: './catalog-browser-transfer-dialog.component.html',
  styleUrls: ['./catalog-browser-transfer-dialog.component.scss']
})
export class CatalogBrowserTransferDialog implements OnInit {

  name: string = '';
  storageTypeId = '';

  constructor(@Inject('STORAGE_TYPES') public storageTypes: StorageType[],
              private dialogRef: MatDialogRef<CatalogBrowserTransferDialog>,
              @Inject(MAT_DIALOG_DATA) contractDefinition?: any) {
  }

  ngOnInit(): void {
  }


  onTransfer() {
    this.dialogRef.close({storageTypeId: this.storageTypeId});
  }

}
