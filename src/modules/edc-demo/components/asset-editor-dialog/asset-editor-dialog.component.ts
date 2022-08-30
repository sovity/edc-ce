import {Component, Inject, OnInit} from '@angular/core';
import {AssetDto, AssetEntryDto, AssetService} from "../../../edc-dmgmt-client";
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
  originator: string = '';
  dataDestination: string = '';

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
          "asset:prop:type": this.type,
          "asset:prop:description": this.description,
          "asset:prop:originator": this.originator,
        }
      },
      dataAddress: JSON.parse(this.dataDestination)
    };

    this.dialogRef.close({ assetEntryDto });
  }
}
