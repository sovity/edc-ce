import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {switchDisabledControls} from '../../utils/form-group-utils';
import {jsonValidator} from '../../validators/json-validator';
import {urlValidator} from '../../validators/url-validator';
import {DataAddressType} from '../data-address-type-select/data-address-type';
import {
  ContractAgreementTransferDialogFormModel,
  ContractAgreementTransferDialogFormValue,
} from './contract-agreement-transfer-dialog-form-model';

/**
 * Handles AngularForms for ContractAgreementTransferDialog
 */
@Injectable()
export class ContractAgreementTransferDialogForm {
  formGroup = this.buildFormGroup();

  /**
   * Quick access to selected data address type
   */
  get dataAddressType(): DataAddressType | null {
    return this.formGroup.controls.dataAddressType.value;
  }

  /**
   * Quick access to full value
   */
  get value(): ContractAgreementTransferDialogFormValue {
    return this.formGroup.value;
  }

  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(): FormGroup<ContractAgreementTransferDialogFormModel> {
    const formGroup: FormGroup<ContractAgreementTransferDialogFormModel> =
      this.formBuilder.nonNullable.group({
        dataAddressType: 'Http' as DataAddressType | null,
        dataDestination: ['', [Validators.required, jsonValidator]],
        httpUrl: ['', [Validators.required, urlValidator]],
      });

    switchDisabledControls<ContractAgreementTransferDialogFormValue>(
      formGroup,
      (value) => {
        const customDataAddressJson =
          value.dataAddressType === 'Custom-Data-Address-Json';

        const http = value.dataAddressType === 'Http';

        return {
          dataAddressType: true,

          // Custom Datasource JSON
          dataDestination: customDataAddressJson,

          // Http Datasource Fields
          httpUrl: http,
        };
      },
    );

    return formGroup;
  }
}
