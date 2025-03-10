/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {FormControl, ɵFormGroupValue} from '@angular/forms';

/**
 * Form Value Type
 */
export type ContractAgreementTransferDialogFormValue =
  ɵFormGroupValue<ContractAgreementTerminationDialogFormModel>;

/**
 * Form Group Template Type
 */
export interface ContractAgreementTerminationDialogFormModel {
  shortReason: FormControl<string>;
  detailedReason: FormControl<string>;
}
