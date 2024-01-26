import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {DataAddressType} from '../../../../../component-library/data-address/data-address-type-select/data-address-type';
import {ActiveFeatureSet} from '../../../../../core/config/active-feature-set';
import {DataCategorySelectItem} from '../../data-category-select/data-category-select-item';
import {AssetEditDialogMode} from '../asset-edit-dialog-mode';
import {AssetAdvancedFormBuilder} from './asset-advanced-form-builder';
import {AssetDatasourceFormBuilder} from './asset-datasource-form-builder';
import {AssetMetadataFormBuilder} from './asset-metadata-form-builder';
import {AssetAdvancedFormModel} from './model/asset-advanced-form-model';
import {AssetDatasourceFormModel} from './model/asset-datasource-form-model';
import {
  AssetEditorDialogFormModel,
  AssetEditorDialogFormValue,
} from './model/asset-editor-dialog-form-model';
import {AssetMetadataFormModel} from './model/asset-metadata-form-model';

/**
 * Handles AngularForms for AssetEditorDialog
 */
@Injectable()
export class AssetEditDialogForm {
  all!: FormGroup<AssetEditorDialogFormModel>;

  /**
   * FormGroup for stepper step "Metadata"
   */
  metadata!: AssetEditorDialogFormModel['metadata'];

  /**
   * FormGroup for stepper step "Advanced"
   */
  advanced!: AssetEditorDialogFormModel['advanced'];

  /**
   * FormGroup for stepper step "Data Source"
   */
  datasource!: AssetEditorDialogFormModel['datasource'];

  /**
   * Quick access to selected data address type
   */
  get dataAddressType(): DataAddressType | null {
    return this.datasource!.controls.dataAddressType.value;
  }

  /**
   * Quick access to selected data category
   */
  get dataCategory(): DataCategorySelectItem | null {
    return this.advanced!!.controls.dataCategory.value;
  }

  /**
   * Quick Access to Dialog Mode
   */
  get mode(): AssetEditDialogMode {
    return this.all.controls.mode.value;
  }

  /**
   * Quick access to full value
   */
  get value(): AssetEditorDialogFormValue {
    return this.all.value;
  }

  get proxyMethod(): boolean {
    return this.datasource!.controls.httpProxyMethod.value;
  }

  get proxyPath(): boolean {
    return this.datasource!.controls.httpProxyPath.value;
  }

  get proxyQueryParams(): boolean {
    return this.datasource!.controls.httpProxyQueryParams.value;
  }

  constructor(
    private formBuilder: FormBuilder,
    private activeFeatureSet: ActiveFeatureSet,
    private assetMetadataFormBuilder: AssetMetadataFormBuilder,
    private assetAdvancedFormBuilder: AssetAdvancedFormBuilder,
    private assetDatasourceFormBuilder: AssetDatasourceFormBuilder,
  ) {}

  reset(initial: AssetEditorDialogFormValue) {
    this.all = this.buildFormGroup(initial);
    this.metadata = this.all.controls.metadata;
    this.advanced = this.all.controls.advanced;
    this.datasource = this.all.controls.datasource;
  }

  buildFormGroup(
    initial: AssetEditorDialogFormValue,
  ): FormGroup<AssetEditorDialogFormModel> {
    const metadata: FormGroup<AssetMetadataFormModel> =
      this.assetMetadataFormBuilder.buildFormGroup(
        initial.metadata!,
        initial.mode!,
      );

    const formGroup: FormGroup<AssetEditorDialogFormModel> =
      this.formBuilder.nonNullable.group({
        mode: [initial.mode as AssetEditDialogMode],
        metadata,
      });

    if (this.activeFeatureSet.hasMdsFields()) {
      const advanced: FormGroup<AssetAdvancedFormModel> =
        this.assetAdvancedFormBuilder.buildFormGroup(initial.advanced);
      formGroup.addControl('advanced', advanced);
    }

    if (initial.mode !== 'EDIT_METADATA') {
      const datasource: FormGroup<AssetDatasourceFormModel> =
        this.assetDatasourceFormBuilder.buildFormGroup(initial.datasource!);
      formGroup.addControl('datasource', datasource);
    }

    return formGroup;
  }

  onHttpHeadersAddClick() {
    this.datasource!.controls.httpHeaders.push(
      this.assetDatasourceFormBuilder.buildHeaderFormGroup({
        headerName: '',
        headerValue: '',
      }),
    );
  }

  onHttpHeadersRemoveClick(index: number) {
    this.datasource!.controls.httpHeaders.removeAt(index);
  }

  onHttpQueryParamsAddClick() {
    this.datasource!.controls.httpQueryParams.push(
      this.assetDatasourceFormBuilder.buildQueryParamFormGroup({
        paramName: '',
        paramValue: '',
      }),
    );
  }

  onHttpQueryParamsRemoveClick(index: number) {
    this.datasource!.controls.httpQueryParams.removeAt(index);
  }

  onNutsLocationsAddClick() {
    this.advanced!.controls.nutsLocations.push(
      this.assetAdvancedFormBuilder.buildRequiredString(''),
    );
  }

  onNutsLocationsRemoveClick(index: number) {
    this.advanced!.controls.nutsLocations.removeAt(index);
  }

  onDataSampleUrlsAddClick() {
    this.advanced!.controls.dataSampleUrls.push(
      this.assetAdvancedFormBuilder.buildRequiredUrl(''),
    );
  }

  onDataSampleUrlsRemoveClick(index: number) {
    this.advanced!.controls.dataSampleUrls.removeAt(index);
  }

  onReferenceFileUrlsAddClick() {
    this.advanced!.controls.referenceFileUrls.push(
      this.assetAdvancedFormBuilder.buildRequiredUrl(''),
    );
  }

  onReferenceFileUrlsRemoveClick(index: number) {
    this.advanced!.controls.referenceFileUrls.removeAt(index);
  }
}
