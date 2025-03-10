/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {ContractAgreementTransferDialogFormValue} from '../../routes/connector-ui/contract-agreement-page/contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-form-model';
import {TransferDataSinkMapper} from './transfer-data-sink-mapper';

@Injectable({providedIn: 'root'})
export class DataAddressMapper {
  constructor(private httpRequestParamsMapper: TransferDataSinkMapper) {}

  buildDataAddressProperties(
    formValue: ContractAgreementTransferDialogFormValue | undefined,
  ): Record<string, string> {
    switch (formValue?.dataAddressType) {
      case 'Custom-Data-Address-Json':
        return JSON.parse(formValue.dataDestination?.trim()!!);
      case 'Http':
        return this.httpRequestParamsMapper.buildHttpDataAddress(formValue);
      default:
        throw new Error(
          `Invalid Data Address Type ${formValue?.dataAddressType}`,
        );
    }
  }
}
