/*
 * Copyright 2022 Eclipse Foundation and Contributors
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     Eclipse Foundation - initial setup of the eclipse-edc/DataDashboard UI
 *     sovity - continued development
 */
import {Component, Inject, OnDestroy} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {EMPTY, Observable, Subject, switchMap} from 'rxjs';
import {catchError, finalize, map, takeUntil, tap} from 'rxjs/operators';
import {IdResponseDto} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {AssetRequestBuilderLegacy} from '../../../../core/services/asset-request-builder-legacy';
import {AssetService} from '../../../../core/services/asset.service';
import {NotificationService} from '../../../../core/services/notification.service';
import {ValidationMessages} from '../../../../core/validators/validation-messages';
import {AssetCreateDialogData} from './asset-create-dialog-data';
import {AssetCreateDialogResult} from './asset-create-dialog-result';
import {AssetAdvancedFormBuilder} from './form/asset-advanced-form-builder';
import {AssetCreateDialogForm} from './form/asset-create-dialog-form';
import {AssetDatasourceFormBuilder} from './form/asset-datasource-form-builder';
import {AssetMetadataFormBuilder} from './form/asset-metadata-form-builder';
import {DATA_SOURCE_HTTP_METHODS} from './form/http-methods';
import {AssetCreateDialogFormValue} from './form/model/asset-create-dialog-form-model';

@Component({
  selector: 'asset-create-dialog',
  templateUrl: './asset-create-dialog.component.html',
  providers: [
    AssetAdvancedFormBuilder,
    AssetDatasourceFormBuilder,
    AssetCreateDialogForm,
    AssetRequestBuilderLegacy,
    AssetMetadataFormBuilder,
  ],
})
export class AssetCreateDialogComponent implements OnDestroy {
  loading = false;

  methods = DATA_SOURCE_HTTP_METHODS;

  constructor(
    private edcApiService: EdcApiService,
    private assetService: AssetService,
    public form: AssetCreateDialogForm,
    public validationMessages: ValidationMessages,
    private assetEntryBuilder: AssetRequestBuilderLegacy,
    private notificationService: NotificationService,
    private dialogRef: MatDialogRef<AssetCreateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: AssetCreateDialogData,
  ) {
    this.form.reset(this.data.initialFormValue);
  }

  onSave() {
    const formValue = this.form.value;

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
          (assets): AssetCreateDialogResult => ({
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
        next: (result: AssetCreateDialogResult) => this.close(result),
        error: (error) => {
          console.error('Failed refreshing asset list!', error);
          this.notificationService.showError('Failed refreshing asset list!');
        },
      });
  }

  private _saveRequest(
    formValue: AssetCreateDialogFormValue,
  ): Observable<IdResponseDto> {
    const createRequest =
      this.assetEntryBuilder.buildAssetCreateRequestLegacy(formValue);
    return this.edcApiService.createAsset(createRequest);
  }

  private close(params: AssetCreateDialogResult) {
    this.dialogRef.close(params);
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
