import {Component, Inject} from '@angular/core';
import {AssetService} from "../../../edc-dmgmt-client";
import {MatDialogRef} from "@angular/material/dialog";
import {StorageType} from "../../models/storage-type";
import {AssetEditorDialogForm} from "./asset-editor-dialog-form";
import {AssetEntryDtoBuilder} from "./asset-entry-dto-builder";


@Component({
  selector: 'edc-demo-asset-editor-dialog',
  templateUrl: './asset-editor-dialog.component.html',
  styleUrls: ['./asset-editor-dialog.component.scss'],
  providers: [AssetEditorDialogForm, AssetEntryDtoBuilder]
})
export class AssetEditorDialog {

  invalidUrlMessage = "Must be valid URL, e.g. https://example.com"
  invalidJsonMessage = "Must be valid JSON"

  constructor(
    public form: AssetEditorDialogForm,
    private assetEntryDtoBuilder: AssetEntryDtoBuilder,
    private assetService: AssetService,
    private dialogRef: MatDialogRef<AssetEditorDialog>,
    @Inject('STORAGE_TYPES') public storageTypes: StorageType[],
  ) {
  }

  onSave() {
    const formValue = this.form.value
    const assetEntryDto = this.assetEntryDtoBuilder.buildAssetEntryDto(formValue)
    this.dialogRef.close({assetEntryDto});
  }
}
