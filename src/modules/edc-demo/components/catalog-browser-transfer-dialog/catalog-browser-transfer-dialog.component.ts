import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {StorageType} from '../../models/storage-type';
import {AssetEntryDto, DataAddress} from "../../../edc-dmgmt-client";


@Component({
    selector: 'edc-demo-catalog-browser-transfer-dialog',
    templateUrl: './catalog-browser-transfer-dialog.component.html',
    styleUrls: ['./catalog-browser-transfer-dialog.component.scss']
})
export class CatalogBrowserTransferDialog implements OnInit {

    name: string = '';
    dataDestination: string = '';
    type: string = '';

    constructor(@Inject('STORAGE_TYPES') public storageTypes: StorageType[],
                private dialogRef: MatDialogRef<CatalogBrowserTransferDialog>,
                @Inject(MAT_DIALOG_DATA) contractDefinition?: any) {
    }

    ngOnInit(): void {
    }


    onTransfer() {
        if (this.type == 'Json') {
            const result = {dataDestination: JSON.parse(this.dataDestination)};
            this.dialogRef.close(result);
        } else if (this.type == 'Rest-Api') {
            const dataAddress: DataAddress = {
                properties: {
                    "type": "HttpData",
                    "baseUrl": this.dataDestination
                }
            }
            const result = {dataDestination: dataAddress};
            this.dialogRef.close(result);
        }
    }

}
