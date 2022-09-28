import {Component, Inject, OnInit} from '@angular/core';
import {
    AssetDto,
    AssetEntryDto,
    AssetService,
    CONNECTOR_DATAMANAGEMENT_API, CONNECTOR_ORIGINATOR
} from "../../../edc-dmgmt-client";
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
  type: string = '';
  description: string = '';
  dataDestination: string = '';
  baseUrl: string = '';
  connectorOriginator: string = '';

  constructor(
      private assetService: AssetService,
      private dialogRef: MatDialogRef<AssetEditorDialog>,
      @Inject('STORAGE_TYPES') public storageTypes: StorageType[],
      @Inject(CONNECTOR_ORIGINATOR) connectorOriginator: string) {
      this.connectorOriginator = connectorOriginator;
  }

  ngOnInit(): void {
  }

  onSave() {
    if (this.type == 'Json') {
      const assetEntryDto: AssetEntryDto = {
        asset: {
          properties: {
            "asset:prop:name": this.name.trim(),
            "asset:prop:version": this.version.trim(),
            "asset:prop:id": this.id.trim(),
            "asset:prop:contenttype": this.contenttype.trim(),
            "asset:prop:description": this.description.trim(),
            "asset:prop:originator": this.connectorOriginator.trim(),
          }
        },
        dataAddress: JSON.parse(this.dataDestination.trim())
      };
      this.dialogRef.close({ assetEntryDto });
    } else if (this.type == 'Rest-Api') {
      const assetEntryDto: AssetEntryDto = {
        asset: {
          properties: {
            "asset:prop:name": this.name.trim(),
            "asset:prop:version": this.version.trim(),
            "asset:prop:id": this.id.trim(),
            "asset:prop:contenttype": this.contenttype.trim(),
            "asset:prop:description": this.description.trim(),
            "asset:prop:originator": this.connectorOriginator.trim(),
          }
        },
        dataAddress: {
          properties: {
            "type": "HttpData",
            "baseUrl": this.baseUrl.trim()
          }
        }
      };
      this.dialogRef.close({ assetEntryDto });
    }
  }
}
