/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {urlValidator} from 'src/app/core/validators/url-validator';
import {validOptionalDateRange} from 'src/app/core/validators/valid-optional-date-range';
import {AssetAdvancedFormModel} from './model/asset-advanced-form-model';
import {AssetCreateDialogFormValue} from './model/asset-create-dialog-form-model';

@Injectable()
export class AssetAdvancedFormBuilder {
  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(
    initial: AssetCreateDialogFormValue['advanced'],
  ): FormGroup<AssetAdvancedFormModel> {
    return this.formBuilder.nonNullable.group({
      dataModel: initial?.dataModel!,
      dataCategory: [initial?.dataCategory || null, Validators.required],
      dataSubcategory: initial?.dataSubcategory || null,
      transportMode: initial?.transportMode || null,
      geoReferenceMethod: initial?.geoReferenceMethod!,
      sovereignLegalName: initial?.sovereignLegalName!,
      geoLocation: initial?.geoLocation!,
      nutsLocations: this.formBuilder.nonNullable.array(
        initial?.nutsLocations?.map((x) => this.buildRequiredString(x)) ?? [],
      ),
      dataSampleUrls: this.formBuilder.array(
        initial?.dataSampleUrls?.map((x) => this.buildRequiredUrl(x)) ?? [],
      ),
      referenceFileUrls: this.formBuilder.nonNullable.array(
        initial?.referenceFileUrls?.map((x) => this.buildRequiredUrl(x)) ?? [],
      ),
      referenceFilesDescription: initial?.referenceFilesDescription!,
      conditionsForUse: initial?.conditionsForUse!,
      dataUpdateFrequency: initial?.dataUpdateFrequency!,
      temporalCoverage: this.formBuilder.group(
        {
          from: initial?.temporalCoverage?.from || null,
          toInclusive: initial?.temporalCoverage?.toInclusive || null,
        },
        {validators: validOptionalDateRange},
      ),
    });
  }

  buildRequiredString(initial: string): FormControl<string> {
    return this.formBuilder.nonNullable.control(initial, Validators.required);
  }

  buildRequiredUrl(initial: string): FormControl<string> {
    return this.formBuilder.nonNullable.control(initial, [
      Validators.required,
      urlValidator,
    ]);
  }
}
