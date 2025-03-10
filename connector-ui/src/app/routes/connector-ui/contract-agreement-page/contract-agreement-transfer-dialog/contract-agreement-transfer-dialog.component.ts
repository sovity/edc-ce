/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {Component, Inject, OnDestroy} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Observable, Subject} from 'rxjs';
import {finalize} from 'rxjs/operators';
import {
  IdResponseDto,
  InitiateCustomTransferRequest,
  InitiateTransferRequest,
} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {DataAddressMapper} from '../../../../core/services/data-address-mapper';
import {NotificationService} from '../../../../core/services/notification.service';
import {ValidationMessages} from '../../../../core/validators/validation-messages';
import {DATA_SINK_HTTP_METHODS} from '../../../../shared/business/edit-asset-form/form/http-methods';
import {ContractAgreementTransferDialogData} from './contract-agreement-transfer-dialog-data';
import {ContractAgreementTransferDialogForm} from './contract-agreement-transfer-dialog-form';
import {ContractAgreementTransferDialogFormValue} from './contract-agreement-transfer-dialog-form-model';
import {ContractAgreementTransferDialogResult} from './contract-agreement-transfer-dialog-result';

@Component({
  selector: 'contract-agreement-transfer-dialog',
  templateUrl: './contract-agreement-transfer-dialog.component.html',
  providers: [ContractAgreementTransferDialogForm],
})
export class ContractAgreementTransferDialogComponent implements OnDestroy {
  loading = false;

  dataSinkMethods = DATA_SINK_HTTP_METHODS;

  constructor(
    public form: ContractAgreementTransferDialogForm,
    public validationMessages: ValidationMessages,
    private dialogRef: MatDialogRef<ContractAgreementTransferDialogComponent>,
    private edcApiService: EdcApiService,
    private notificationService: NotificationService,
    private dataAddressMapper: DataAddressMapper,
    @Inject(MAT_DIALOG_DATA) public data: ContractAgreementTransferDialogData,
  ) {}

  onSave() {
    if (this.loading && !this.form.all.valid) {
      return;
    }

    this.initiateTransfer();
  }

  private initiateTransfer() {
    this.loading = true;
    this.form.all.disable();

    const value = this.form.value;
    let request$: Observable<IdResponseDto>;
    if (value.dataAddressType === 'Custom-Transfer-Process-Request') {
      const request = this.buildCustomTransferRequest(value);
      request$ = this.edcApiService.initiateCustomTransfer(request);
    } else {
      const request = this.buildTransferRequest(value);
      request$ = this.edcApiService.initiateTransfer(request);
    }

    request$
      .pipe(
        finalize(() => {
          this.loading = false;
          this.form.all.enable();
        }),
      )
      .subscribe({
        next: (response) =>
          this.close({
            transferProcessId: response.id!,
            contractId: this.data.contractId,
          }),
        error: (err) => {
          this.notificationService.showError('Failed initiating transfer!');
          console.error('Failed initiating transfer', err);
        },
      });
  }

  private close(params: ContractAgreementTransferDialogResult) {
    this.dialogRef.close(params);
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }

  private buildTransferRequest(
    value: ContractAgreementTransferDialogFormValue,
  ): InitiateTransferRequest {
    const dataSinkProperties =
      this.dataAddressMapper.buildDataAddressProperties(value) ?? {};

    return {
      contractAgreementId: this.data.contractId,
      transferProcessProperties: {},
      dataSinkProperties,
      transferType: 'HttpData-PUSH',
    };
  }

  private buildCustomTransferRequest(
    value: ContractAgreementTransferDialogFormValue,
  ): InitiateCustomTransferRequest {
    return {
      contractAgreementId: this.data.contractId,
      transferProcessRequestJsonLd: value.transferProcessRequest!,
    };
  }
}
