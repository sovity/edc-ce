import {Component, Inject, OnDestroy} from '@angular/core';
import {AssetService} from "../../../edc-dmgmt-client";
import {MatDialogRef} from "@angular/material/dialog";
import {StorageType} from "../../models/storage-type";
import {AssetEditorDialogForm} from "./asset-editor-dialog-form";
import {AssetEntryBuilder} from "../../services/asset-entry-builder";
import {finalize, takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import {NotificationService} from "../../services/notification.service";
import {AssetEditorDialogResult} from "./asset-editor-dialog-result";


@Component({
  selector: 'edc-demo-asset-editor-dialog',
  templateUrl: './asset-editor-dialog.component.html',
  providers: [AssetEditorDialogForm, AssetEntryBuilder]
})
export class AssetEditorDialog implements OnDestroy {

  invalidUrlMessage = "Must be valid URL, e.g. https://example.com"
  invalidJsonMessage = "Must be valid JSON"
  invalidWhitespacesMessage = "Must not contain whitespaces."
  invalidIdPrefix = "ID must start with \"urn:artifact:\"."

  loading = false

  constructor(
    public form: AssetEditorDialogForm,
    private assetEntryDtoBuilder: AssetEntryBuilder,
    private notificationService: NotificationService,
    private assetService: AssetService,
    private dialogRef: MatDialogRef<AssetEditorDialog>,
    @Inject('STORAGE_TYPES') public storageTypes: StorageType[],
  ) {
  }

  onSave() {
    const formValue = this.form.value
    const assetEntryDto = this.assetEntryDtoBuilder.buildAssetEntry(formValue)

    this.form.all.disable();
    this.loading = true;
    this.assetService.createAsset(assetEntryDto)
      .pipe(
        takeUntil(this.ngOnDestroy$),
        finalize(() => {
          this.form.all.enable();
          this.loading = false;
        })
      )
      .subscribe({
        complete: () => {
          this.notificationService.showInfo("Successfully created");
          this.close({refreshList: true});
        },
        error: error => {
          console.error("Failed creating asset!", error);
          this.notificationService.showError("Failed creating asset!");
        }
      })
  }

  private close(params: AssetEditorDialogResult) {
    this.dialogRef.close(params)
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
