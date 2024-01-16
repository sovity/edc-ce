import {Component, Inject, OnDestroy} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {EMPTY, Observable, Subject, switchMap} from 'rxjs';
import {catchError, finalize, map, takeUntil, tap} from 'rxjs/operators';
import {IdResponseDto} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {AssetCreateRequestBuilder} from '../../../../core/services/asset-create-request-builder';
import {AssetService} from '../../../../core/services/asset.service';
import {NotificationService} from '../../../../core/services/notification.service';
import {ValidationMessages} from '../../../../core/validators/validation-messages';
import {AssetEditDialogData} from './asset-edit-dialog-data';
import {AssetEditDialogResult} from './asset-edit-dialog-result';
import {AssetAdvancedFormBuilder} from './form/asset-advanced-form-builder';
import {AssetDatasourceFormBuilder} from './form/asset-datasource-form-builder';
import {AssetEditDialogForm} from './form/asset-edit-dialog-form';
import {AssetMetadataFormBuilder} from './form/asset-metadata-form-builder';
import {DATA_SOURCE_HTTP_METHODS} from './form/http-methods';
import {AssetEditorDialogFormValue} from './form/model/asset-editor-dialog-form-model';

@Component({
  selector: 'asset-edit-dialog',
  templateUrl: './asset-edit-dialog.component.html',
  providers: [
    AssetAdvancedFormBuilder,
    AssetDatasourceFormBuilder,
    AssetEditDialogForm,
    AssetCreateRequestBuilder,
    AssetMetadataFormBuilder,
  ],
})
export class AssetEditDialogComponent implements OnDestroy {
  loading = false;

  methods = DATA_SOURCE_HTTP_METHODS;

  constructor(
    private edcApiService: EdcApiService,
    private assetService: AssetService,
    public form: AssetEditDialogForm,
    public validationMessages: ValidationMessages,
    private assetEntryBuilder: AssetCreateRequestBuilder,
    private notificationService: NotificationService,
    private dialogRef: MatDialogRef<AssetEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: AssetEditDialogData,
  ) {
    this.form.reset(this.data.initialFormValue);
  }

  onSave() {
    const formValue = this.form.value;

    // Workaround around disabled controls not being included in the form value
    if (formValue.mode !== 'CREATE') {
      formValue.metadata!.id = this.form.metadata.controls.id.getRawValue();
    }

    this.form.all.disable();
    this.loading = true;
    this._saveRequest(formValue)
      .pipe(
        // Save Asset
        takeUntil(this.ngOnDestroy$),
        tap(() => {
          this.notificationService.showInfo('Successfully saved asset');
        }),
        catchError((error) => {
          console.error('Failed saving asset!', error);
          this.notificationService.showError('Failed saving asset!');
          this.form.all.enable();
          return EMPTY;
        }),
        switchMap(() => this.assetService.fetchAssets()),
        map(
          (assets): AssetEditDialogResult => ({
            refreshedList: assets,
            asset: assets?.find(
              (it) => it.assetId === this.form.value.metadata?.id,
            )!,
          }),
        ),
        finalize(() => {
          this.loading = false;
        }),
      )
      .subscribe({
        next: (result: AssetEditDialogResult) => this.close(result),
        error: (error) => {
          console.error('Failed refreshing asset list!', error);
          this.notificationService.showError('Failed refreshing asset list!');
        },
      });
  }

  private _saveRequest(
    formValue: AssetEditorDialogFormValue,
  ): Observable<IdResponseDto> {
    const mode = formValue.mode;

    if (mode === 'CREATE') {
      const createRequest =
        this.assetEntryBuilder.buildAssetCreateRequest(formValue);
      return this.edcApiService.createAsset(createRequest);
    }

    if (mode === 'EDIT_METADATA') {
      const assetId = formValue.metadata?.id!;
      const editRequest =
        this.assetEntryBuilder.buildEditMetadataRequest(formValue);
      return this.edcApiService.editAssetMetadata(assetId, editRequest);
    }

    throw new Error(`Unsupported mode: ${mode}`);
  }

  private close(params: AssetEditDialogResult) {
    this.dialogRef.close(params);
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
