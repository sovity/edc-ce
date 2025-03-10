/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Inject, OnDestroy} from '@angular/core';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Observable, Subject} from 'rxjs';
import {finalize} from 'rxjs/operators';
import {IdResponseDto} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {NotificationService} from '../../../../core/services/notification.service';
import {ContractAgreementTerminationDialogData} from './contract-agreement-termination-dialog-data';
import {ContractAgreementTerminationDialogForm} from './contract-agreement-termination-dialog-form';
import {ContractAgreementTerminationDialogResult} from './contract-agreement-termination-dialog-result';

@Component({
  selector: 'contract-agreement-transfer-dialog',
  templateUrl: './contract-agreement-termination-dialog.component.html',
  providers: [ContractAgreementTerminationDialogForm],
})
export class ContractAgreementTerminationDialogComponent implements OnDestroy {
  loading = false;
  checkboxChecked = false;

  constructor(
    public form: ContractAgreementTerminationDialogForm,
    private dialogRef: MatDialogRef<ContractAgreementTerminationDialogComponent>,
    private edcApiService: EdcApiService,
    private notificationService: NotificationService,
    @Inject(MAT_DIALOG_DATA)
    public data: ContractAgreementTerminationDialogData,
  ) {}

  public onCheckboxChange($event: MatCheckboxChange) {
    this.checkboxChecked = $event.checked;
  }

  onSave() {
    if (this.loading && !this.form.all.valid) {
      return;
    }

    this.initiateTermination();
  }

  private initiateTermination() {
    this.loading = true;
    this.form.all.disable();

    const value = this.form.value;
    let request$: Observable<IdResponseDto>;
    request$ = this.edcApiService.terminateContractAgreement({
      contractAgreementId: this.data.contractId,
      contractTerminationRequest: {
        reason: value.shortReason!,
        detail: value.detailedReason!,
      },
    });

    request$
      .pipe(
        finalize(() => {
          this.loading = false;
          this.form.all.enable();
        }),
      )
      .subscribe({
        next: (response) => {
          this.close({
            contractId: response.id,
            lastUpdatedTime: response.lastUpdatedDate,
          });
        },
        error: (error) => {
          this.notificationService.showError('Contract termination failed!');
          console.error('Contract termination failed', error);
        },
      });
  }

  private close(params: ContractAgreementTerminationDialogResult) {
    this.dialogRef.close(params);
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
