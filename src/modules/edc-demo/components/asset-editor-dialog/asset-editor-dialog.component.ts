import {Component, Inject, OnInit} from '@angular/core';
import { AssetEntryDto, AssetService} from "../../../mgmt-api-client";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {StorageType} from "../../models/storage-type";


@Component({
  selector: 'edc-demo-asset-editor-dialog',
  templateUrl: './asset-editor-dialog.component.html',
  styleUrls: ['./asset-editor-dialog.component.scss']
})
export class AssetEditorDialog implements OnInit {

  id: string = '';
  version: string = '';
  name: string = '';
  contenttype: string = '';

  storageTypeId: string = 'AzureStorage';
  account: string = '';
  container: string = 'src-container';
  blobname: string = '';

  constructor(private assetService: AssetService, private dialogRef: MatDialogRef<AssetEditorDialog>,
      @Inject('STORAGE_TYPES') public storageTypes: StorageType[]) {
  }

  ngOnInit(): void {
  }

  onSave() {
    const assetEntryDto: AssetEntryDto = {
      asset: {
        properties: {
          "asset:prop:name": this.name,
          "asset:prop:version": this.version,
          "asset:prop:id": this.id,
          "asset:prop:contenttype": this.contenttype,
        }
      },
      dataAddress: {
        properties: {
          "type": this.storageTypeId,
          "account": this.account,
          "container": this.container,
          "blobname": this.blobname,
          "keyName": `${this.account}-key1`
        },
      }
    };

    this.dialogRef.close({ assetEntryDto });
  }
}
