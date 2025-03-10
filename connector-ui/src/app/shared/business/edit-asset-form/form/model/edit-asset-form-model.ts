/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  FormControl,
  FormGroup,
  UntypedFormGroup,
  ɵFormGroupValue,
} from '@angular/forms';
import {AssetAdvancedFormModel} from './asset-advanced-form-model';
import {AssetDatasourceFormModel} from './asset-datasource-form-model';
import {AssetEditDialogMode} from './asset-edit-dialog-mode';
import {AssetGeneralFormModel} from './asset-general-form-model';
import {DataOfferPublishMode} from './data-offer-publish-mode';

/**
 * Form Model for Edit Asset Form
 */
export interface EditAssetFormModel {
  mode: FormControl<AssetEditDialogMode>;
  publishMode: FormControl<DataOfferPublishMode>;
  policyControls: UntypedFormGroup;
  general: FormGroup<AssetGeneralFormModel>;
  datasource: FormGroup<AssetDatasourceFormModel>;
  advanced?: FormGroup<AssetAdvancedFormModel>;
}

/**
 * Form Value for Edit Asset Form
 */
export type EditAssetFormValue = ɵFormGroupValue<EditAssetFormModel>;
