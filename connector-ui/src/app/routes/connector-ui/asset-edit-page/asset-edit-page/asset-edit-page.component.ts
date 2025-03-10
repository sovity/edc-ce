/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
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
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {EMPTY, Observable, catchError, finalize, tap} from 'rxjs';
import {
  DataOfferPublishType,
  IdResponseDto,
  UiAssetEditRequest,
} from '@sovity.de/edc-client';
import {EdcApiService} from 'src/app/core/services/api/edc-api.service';
import {AssetRequestBuilder} from 'src/app/core/services/asset-request-builder';
import {AssetService} from 'src/app/core/services/asset.service';
import {Fetched} from 'src/app/core/services/models/fetched';
import {UiAssetMapped} from 'src/app/core/services/models/ui-asset-mapped';
import {NotificationService} from 'src/app/core/services/notification.service';
import {editAssetFormRequiredViewProviders} from '../../../../shared/business/edit-asset-form/edit-asset-form-required-providers';
import {EditAssetForm} from '../../../../shared/business/edit-asset-form/form/edit-asset-form';
import {EditAssetFormInitializer} from '../../../../shared/business/edit-asset-form/form/edit-asset-form-initializer';
import {EditAssetFormValue} from '../../../../shared/business/edit-asset-form/form/model/edit-asset-form-model';
import {ExpressionFormHandler} from '../../../../shared/business/policy-editor/editor/expression-form-handler';

@Component({
  selector: 'asset-edit-page',
  templateUrl: './asset-edit-page.component.html',
  providers: [EditAssetFormInitializer, AssetRequestBuilder],
  viewProviders: editAssetFormRequiredViewProviders,
})
export class AssetEditPageComponent implements OnInit {
  asset: Fetched<UiAssetMapped | undefined> = new Fetched(
    'loading',
    undefined,
    undefined,
  );
  isLoading = false;

  constructor(
    private editAssetFormInitializer: EditAssetFormInitializer,
    private form: EditAssetForm,
    private assetRequestBuilder: AssetRequestBuilder,
    private edcApiService: EdcApiService,
    private assetServiceMapped: AssetService,
    private notificationService: NotificationService,
    private router: Router,
    private route: ActivatedRoute,
    private expressionFormHandler: ExpressionFormHandler,
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      if (params.id) {
        this.assetServiceMapped
          .fetchAssets()
          .pipe(
            Fetched.wrap({
              failureMessage: 'Failed fetching asset list.',
            }),
          )
          .pipe(
            Fetched.map((assets): UiAssetMapped | undefined =>
              assets.find((asset) => asset.assetId === params.id),
            ),
          )
          .subscribe((asset) => {
            this.asset = asset;

            if (asset.isReady) {
              this.form.reset(
                this.editAssetFormInitializer.forEdit(asset.data!),
              );
            }
          });
      } else {
        this.form.reset(this.editAssetFormInitializer.forCreate());
        this.asset.state = 'ready';
      }
    });
  }

  onSubmit() {
    const formValue = this.form.value;

    // Workaround around disabled controls not being included in the form value
    if (formValue.mode !== 'CREATE') {
      formValue.general!.id = this.form.general.controls.id.getRawValue();
    }

    this.form.all.disable();
    this.isLoading = true;

    this._saveRequest(formValue)
      .pipe(
        tap(() => {
          this.notificationService.showInfo('Successfully saved asset');
        }),
        catchError((error) => {
          console.error('Failed saving asset!', error);
          this.notificationService.showError('Failed saving asset!');
          this.form.all.enable();
          return EMPTY;
        }),
        finalize(() => {
          this.isLoading = false;
        }),
      )
      .subscribe(() => this.router.navigate(['my-assets']));
  }

  private _saveRequest(
    formValue: EditAssetFormValue,
  ): Observable<IdResponseDto> {
    const assetId = formValue.general!.id!;
    const mode = this.form.mode;
    const publishMode = formValue.publishMode!;

    if (mode === 'CREATE') {
      const assetCreateRequest =
        this.assetRequestBuilder.buildAssetCreateRequest(formValue);

      if (publishMode === 'PUBLISH_UNRESTRICTED') {
        return this.edcApiService.createDataOffer({
          dataOfferCreateRequest: {
            asset: assetCreateRequest,
            publishType: DataOfferPublishType.PublishUnrestricted,
            policyExpression: this.expressionFormHandler.toUiPolicyExpression(),
          },
        });
      } else if (publishMode === 'PUBLISH_RESTRICTED') {
        return this.edcApiService.createDataOffer({
          dataOfferCreateRequest: {
            asset: assetCreateRequest,
            publishType: DataOfferPublishType.PublishRestricted,
            policyExpression: this.expressionFormHandler.toUiPolicyExpression(),
          },
        });
      } else {
        return this.edcApiService.createDataOffer({
          dataOfferCreateRequest: {
            asset: assetCreateRequest,
            publishType: DataOfferPublishType.DontPublish,
          },
        });
      }
    }

    if (mode === 'EDIT') {
      const asset = this.asset.data;

      const editRequest: UiAssetEditRequest = {
        ...this.assetRequestBuilder.buildAssetEditRequest(formValue),
        customJsonAsString: asset?.customJsonAsString,
        customJsonLdAsString: asset?.customJsonLdAsString,
        privateCustomJsonAsString: asset?.privateCustomJsonAsString,
        privateCustomJsonLdAsString: asset?.privateCustomJsonLdAsString,
      };

      return this.edcApiService.editAsset(assetId, editRequest);
    }

    throw new Error(`Unsupported mode: ${mode}`);
  }
}
