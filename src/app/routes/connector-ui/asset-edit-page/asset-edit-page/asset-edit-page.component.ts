import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {EMPTY, Observable, catchError, finalize, tap} from 'rxjs';
import {IdResponseDto} from '@sovity.de/edc-client';
import {AssetAdvancedFormBuilder} from 'src/app/component-library/edit-asset-form/edit-asset-form/form/asset-advanced-form-builder';
import {AssetDatasourceFormBuilder} from 'src/app/component-library/edit-asset-form/edit-asset-form/form/asset-datasource-form-builder';
import {AssetGeneralFormBuilder} from 'src/app/component-library/edit-asset-form/edit-asset-form/form/asset-general-form-builder';
import {EditAssetForm} from 'src/app/component-library/edit-asset-form/edit-asset-form/form/edit-asset-form';
import {EditAssetFormInitializer} from 'src/app/component-library/edit-asset-form/edit-asset-form/form/edit-asset-form-initializer';
import {EditAssetFormValue} from 'src/app/component-library/edit-asset-form/edit-asset-form/form/model/edit-asset-form-model';
import {EdcApiService} from 'src/app/core/services/api/edc-api.service';
import {AssetRequestBuilder} from 'src/app/core/services/asset-request-builder';
import {AssetService} from 'src/app/core/services/asset.service';
import {Fetched} from 'src/app/core/services/models/fetched';
import {UiAssetMapped} from 'src/app/core/services/models/ui-asset-mapped';
import {NotificationService} from 'src/app/core/services/notification.service';

@Component({
  selector: 'asset-edit-page',
  templateUrl: './asset-edit-page.component.html',
  providers: [EditAssetFormInitializer, AssetRequestBuilder],
  viewProviders: [
    EditAssetForm,
    AssetGeneralFormBuilder,
    AssetDatasourceFormBuilder,
    AssetAdvancedFormBuilder,
  ],
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
    const mode = this.form.mode;

    if (mode === 'CREATE') {
      const createRequest =
        this.assetRequestBuilder.buildAssetCreateRequest(formValue);
      return this.edcApiService.createAsset(createRequest);
    }

    if (mode === 'EDIT') {
      const assetId = formValue.general?.id!;
      const editRequest =
        this.assetRequestBuilder.buildAssetEditRequest(formValue);
      return this.edcApiService.editAsset(assetId, editRequest);
    }

    throw new Error(`Unsupported mode: ${mode}`);
  }
}
