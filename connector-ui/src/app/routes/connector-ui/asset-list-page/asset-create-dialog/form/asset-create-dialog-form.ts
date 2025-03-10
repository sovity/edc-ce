/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ActiveFeatureSet} from '../../../../../core/config/active-feature-set';
import {DataAddressType} from '../../../../../shared/form-elements/data-address-type-select/data-address-type';
import {DataCategorySelectItem} from '../../../../../shared/form-elements/data-category-select/data-category-select-item';
import {AssetAdvancedFormBuilder} from './asset-advanced-form-builder';
import {AssetDatasourceFormBuilder} from './asset-datasource-form-builder';
import {AssetMetadataFormBuilder} from './asset-metadata-form-builder';
import {AssetAdvancedFormModel} from './model/asset-advanced-form-model';
import {
  AssetCreateDialogFormModel,
  AssetCreateDialogFormValue,
} from './model/asset-create-dialog-form-model';
import {AssetDatasourceFormModel} from './model/asset-datasource-form-model';
import {AssetMetadataFormModel} from './model/asset-metadata-form-model';

/**
 * Handles AngularForms for AssetCreateDialog
 */
@Injectable()
export class AssetCreateDialogForm {
  all!: FormGroup<AssetCreateDialogFormModel>;

  /**
   * FormGroup for stepper step "Metadata"
   */
  metadata!: AssetCreateDialogFormModel['metadata'];

  /**
   * FormGroup for stepper step "Advanced"
   */
  advanced!: AssetCreateDialogFormModel['advanced'];

  /**
   * FormGroup for stepper step "Data Source"
   */
  datasource!: AssetCreateDialogFormModel['datasource'];

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
    return this.advanced!.controls.dataCategory.value;
  }

  /**
   * Quick access to full value
   */
  get value(): AssetCreateDialogFormValue {
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

  reset(initial: AssetCreateDialogFormValue) {
    this.all = this.buildFormGroup(initial);
    this.metadata = this.all.controls.metadata;
    this.advanced = this.all.controls.advanced;
    this.datasource = this.all.controls.datasource;
  }

  buildFormGroup(
    initial: AssetCreateDialogFormValue,
  ): FormGroup<AssetCreateDialogFormModel> {
    const metadata: FormGroup<AssetMetadataFormModel> =
      this.assetMetadataFormBuilder.buildFormGroup(initial.metadata!);

    const datasource: FormGroup<AssetDatasourceFormModel> =
      this.assetDatasourceFormBuilder.buildFormGroup(initial.datasource!);

    const formGroup: FormGroup<AssetCreateDialogFormModel> =
      this.formBuilder.nonNullable.group({
        metadata,
        datasource,
      });

    if (this.activeFeatureSet.hasMdsFields()) {
      const advanced: FormGroup<AssetAdvancedFormModel> =
        this.assetAdvancedFormBuilder.buildFormGroup(initial.advanced);
      formGroup.addControl('advanced', advanced);
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
