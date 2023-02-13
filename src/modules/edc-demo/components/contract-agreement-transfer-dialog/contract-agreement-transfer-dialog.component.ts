import {Component, Inject, OnDestroy} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Subject} from 'rxjs';
import {finalize} from 'rxjs/operators';
import {
  ContractAgreementService,
  DataAddressDto,
} from '../../../edc-dmgmt-client';
import {AssetEntryBuilder} from '../../services/asset-entry-builder';
import {NotificationService} from '../../services/notification.service';
import {ValidationMessages} from '../../validators/validation-messages';
import {ContractAgreementTransferDialogData} from './contract-agreement-transfer-dialog-data';
import {ContractAgreementTransferDialogForm} from './contract-agreement-transfer-dialog-form';
import {ContractAgreementTransferDialogFormValue} from './contract-agreement-transfer-dialog-form-model';
import {ContractAgreementTransferDialogResult} from './contract-agreement-transfer-dialog-result';

@Component({
  selector: 'edc-demo-contract-agreement-transfer-dialog',
  templateUrl: './contract-agreement-transfer-dialog.component.html',
  providers: [ContractAgreementTransferDialogForm, AssetEntryBuilder],
})
export class ContractAgreementTransferDialog implements OnDestroy {
  loading = false;

  constructor(
    public form: ContractAgreementTransferDialogForm,
    public validationMessages: ValidationMessages,
    private dialogRef: MatDialogRef<ContractAgreementTransferDialog>,
    private contractAgreementService: ContractAgreementService,
    private notificationService: NotificationService,
    @Inject(MAT_DIALOG_DATA) private data: ContractAgreementTransferDialogData,
  ) {}

  onSave() {
    if (this.loading && !this.form.formGroup.valid) {
      return;
    }
    this.loading = true;
    this.form.formGroup.disable();

    this.contractAgreementService
      .initiateTransfer(
        this.data.contractId,
        this.buildDataAddressDto(this.form.value),
      )
      .pipe(
        finalize(() => {
          this.loading = false;
          this.form.formGroup.enable();
        }),
      )
      .subscribe({
        next: (transferProcessId) =>
          this.close({
            transferProcessId,
            contractId: this.data.contractId,
          }),
        error: (err) => {
          this.notificationService.showError('Failed initiating transfer!');
          console.error('Failed initiating transfer', err);
        },
      });
  }

  private buildDataAddressDto(
    formValue: ContractAgreementTransferDialogFormValue,
  ): DataAddressDto {
    switch (formValue.dataAddressType) {
      case 'Custom-Data-Address-Json':
        return JSON.parse(formValue.dataDestination?.trim()!!);
      case 'Http':
        return {
          properties: {
            type: 'HttpData',
            baseUrl: formValue.httpUrl!!.trim(),
          },
        };
      default:
        throw new Error(
          `Invalid Data Address Type ${formValue.dataAddressType}`,
        );
    }
  }

  private close(params: ContractAgreementTransferDialogResult) {
    this.dialogRef.close(params);
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
