/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  FormArray,
  FormControl,
  FormGroup,
  ɵFormGroupValue,
} from '@angular/forms';
import {DataCategorySelectItem} from '../../../../../../shared/form-elements/data-category-select/data-category-select-item';
import {DataSubcategorySelectItem} from '../../../../../../shared/form-elements/data-subcategory-select/data-subcategory-select-item';
import {TransportModeSelectItem} from '../../../../../../shared/form-elements/transport-mode-select/transport-mode-select-item';
import {TemporalCoverageFormModel} from './temporal-coverage-form-model';

/**
 * Form Model for AssetCreateDialog > Advanced
 * (MDS Properties)
 */
export interface AssetAdvancedFormModel {
  dataCategory: FormControl<DataCategorySelectItem | null>;
  dataSubcategory: FormControl<DataSubcategorySelectItem | null>;
  dataModel: FormControl<string>;
  geoReferenceMethod: FormControl<string>;
  transportMode: FormControl<TransportModeSelectItem | null>;
  sovereignLegalName: FormControl<string>;
  geoLocation: FormControl<string>;
  nutsLocations: FormArray<FormControl<string>>;
  dataSampleUrls: FormArray<FormControl<string>>;
  referenceFileUrls: FormArray<FormControl<string>>;
  referenceFilesDescription: FormControl<string>;
  conditionsForUse: FormControl<string>;
  dataUpdateFrequency: FormControl<string>;
  temporalCoverage: FormGroup<TemporalCoverageFormModel>;
}

/**
 * Form Value for AssetCreateDialog > Advanced
 */
export type AssetAdvancedFormValue = ɵFormGroupValue<AssetAdvancedFormModel>;
