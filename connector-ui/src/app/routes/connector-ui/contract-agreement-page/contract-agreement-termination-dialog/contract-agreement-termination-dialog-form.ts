/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  ContractAgreementTerminationDialogFormModel,
  ContractAgreementTransferDialogFormValue,
} from './contract-agreement-termination-dialog-form-model';

/**
 * Handles AngularForms for ContractAgreementTerminationDialog
 */
@Injectable()
export class ContractAgreementTerminationDialogForm {
  all = this.buildFormGroup();

  /**
   * Quick access to full value
   */
  get value(): ContractAgreementTransferDialogFormValue {
    return this.all.value;
  }

  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(): FormGroup<ContractAgreementTerminationDialogFormModel> {
    const formGroup = this.formBuilder.nonNullable.group({
      shortReason: ['Terminated by user', Validators.required],
      detailedReason: ['', [Validators.required, Validators.maxLength(1000)]],
    });
    formGroup.controls.shortReason.disable();
    return formGroup;
  }
}
