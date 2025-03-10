/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {PolicyDefinitionDto} from '@sovity.de/edc-client';
import {DataOfferFormValidators} from 'src/app/core/validators/data-offer-form-validators';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';
import {noWhitespacesOrColonsValidator} from '../../../../core/validators/no-whitespaces-or-colons-validator';
import {
  ContractDefinitionEditorDialogFormModel,
  ContractDefinitionEditorDialogFormValue,
} from './contract-definition-editor-dialog-form-model';

/**
 * Handles AngularForms for ContractDefinitionEditorDialog
 */
@Injectable()
export class ContractDefinitionEditorDialogForm {
  group = this.buildFormGroup();

  /**
   * Quick access to full value
   */
  get value(): ContractDefinitionEditorDialogFormValue {
    return this.group.value;
  }

  constructor(
    private formBuilder: FormBuilder,
    private validators: DataOfferFormValidators,
  ) {}

  buildFormGroup(): FormGroup<ContractDefinitionEditorDialogFormModel> {
    return this.formBuilder.nonNullable.group({
      id: [
        '',
        [Validators.required, noWhitespacesOrColonsValidator],
        [this.validators.contractDefinitionIdExistsValidator],
      ],
      accessPolicy: [null as PolicyDefinitionDto | null, Validators.required],
      contractPolicy: [null as PolicyDefinitionDto | null, Validators.required],
      assets: [[] as UiAssetMapped[], Validators.required],
    });
  }
}
