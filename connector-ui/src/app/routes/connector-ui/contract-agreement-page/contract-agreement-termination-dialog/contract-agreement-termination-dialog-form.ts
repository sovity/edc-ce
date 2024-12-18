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
