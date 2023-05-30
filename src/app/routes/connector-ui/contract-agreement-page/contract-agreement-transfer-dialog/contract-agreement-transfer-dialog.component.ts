import {Component, Inject, OnDestroy} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Subject} from 'rxjs';
import {finalize} from 'rxjs/operators';
import {
  ContractAgreementService,
  DataAddressDto,
} from '../../../../core/services/api/legacy-managent-api-client';
import {AssetEntryBuilder} from '../../../../core/services/asset-entry-builder';
import {HttpRequestParamsMapper} from '../../../../core/services/http-params-mapper.service';
import {NotificationService} from '../../../../core/services/notification.service';
import {ValidationMessages} from '../../../../core/validators/validation-messages';
import {ContractAgreementTransferDialogData} from './contract-agreement-transfer-dialog-data';
import {ContractAgreementTransferDialogForm} from './contract-agreement-transfer-dialog-form';
import {ContractAgreementTransferDialogFormValue} from './contract-agreement-transfer-dialog-form-model';
import {ContractAgreementTransferDialogResult} from './contract-agreement-transfer-dialog-result';

@Component({
  selector: 'contract-agreement-transfer-dialog',
  templateUrl: './contract-agreement-transfer-dialog.component.html',
  providers: [ContractAgreementTransferDialogForm, AssetEntryBuilder],
})
export class ContractAgreementTransferDialogComponent implements OnDestroy {
  loading = false;

  methods = ['POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS', 'HEAD'];

  constructor(
    public form: ContractAgreementTransferDialogForm,
    public validationMessages: ValidationMessages,
    private dialogRef: MatDialogRef<ContractAgreementTransferDialogComponent>,
    private contractAgreementService: ContractAgreementService,
    private notificationService: NotificationService,
    private httpRequestParamsMapper: HttpRequestParamsMapper,
    @Inject(MAT_DIALOG_DATA) private data: ContractAgreementTransferDialogData,
  ) {}

  onSave() {
    if (this.loading && !this.form.all.valid) {
      return;
    }
    this.loading = true;
    this.form.all.disable();

    this.contractAgreementService
      .initiateTransfer(
        this.data.contractId,
        this.buildDataAddressDto(this.form.value),
      )
      .pipe(
        finalize(() => {
          this.loading = false;
          this.form.all.enable();
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
        return this.httpRequestParamsMapper.buildHttpDataAddressDto(formValue);
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
