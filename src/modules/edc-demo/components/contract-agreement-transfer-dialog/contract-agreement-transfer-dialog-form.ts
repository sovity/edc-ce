import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {switchDisabledControls} from '../../utils/form-group-utils';
import {jsonValidator} from '../../validators/json-validator';
import {urlValidator} from '../../validators/url-validator';
import {HttpDatasourceAuthHeaderType} from '../asset-editor-dialog/model/http-datasource-auth-header-type';
import {DataAddressType} from '../data-address-type-select/data-address-type';
import {
  ContractAgreementTransferDialogFormModel,
  ContractAgreementTransferDialogFormValue,
} from './contract-agreement-transfer-dialog-form-model';
import {HttpDatasinkHeaderFormModel} from './http-datasink-header-form-model';

/**
 * Handles AngularForms for ContractAgreementTransferDialog
 */
@Injectable()
export class ContractAgreementTransferDialogForm {
  all = this.buildFormGroup();

  /**
   * Quick access to selected data address type
   */
  get dataAddressType(): DataAddressType | null {
    return this.all.controls.dataAddressType.value;
  }

  /**
   * Quick access to full value
   */
  get value(): ContractAgreementTransferDialogFormValue {
    return this.all.value;
  }

  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(): FormGroup<ContractAgreementTransferDialogFormModel> {
    const all: FormGroup<ContractAgreementTransferDialogFormModel> =
      this.formBuilder.nonNullable.group({
        dataAddressType: 'Http' as DataAddressType,
        dataDestination: ['', [Validators.required, jsonValidator]],

        // Http Datasink Fields
        httpUrl: ['', [Validators.required, urlValidator]],
        httpMethod: ['POST', Validators.required],

        httpAuthHeaderType: ['None' as HttpDatasourceAuthHeaderType],
        httpAuthHeaderName: ['', Validators.required],
        httpAuthHeaderValue: ['', Validators.required],
        httpAuthHeaderSecretName: ['', Validators.required],

        httpHeaders: this.formBuilder.array(
          new Array<FormGroup<HttpDatasinkHeaderFormModel>>(),
        ),
      });

    switchDisabledControls<ContractAgreementTransferDialogFormValue>(
      all,
      (value) => {
        const customDataAddressJson =
          value.dataAddressType === 'Custom-Data-Address-Json';

        const http = value.dataAddressType === 'Http';
        const httpAuth = value.httpAuthHeaderType !== 'None';
        const httpAuthByValue = value.httpAuthHeaderType === 'Value';
        const httpAuthByVault = value.httpAuthHeaderType === 'Vault-Secret';

        return {
          dataAddressType: true,

          // Custom Datasink JSON
          dataDestination: customDataAddressJson,

          // Http Datasink Fields
          httpUrl: http,
          httpMethod: http,

          httpAuthHeaderType: http,
          httpAuthHeaderName: http && httpAuth,
          httpAuthHeaderValue: http && httpAuthByValue,
          httpAuthHeaderSecretName: http && httpAuthByVault,

          httpHeaders: http,
        };
      },
    );
    return all;
  }

  buildHeaderFormGroup(): FormGroup<HttpDatasinkHeaderFormModel> {
    return this.formBuilder.nonNullable.group({
      headerName: ['', Validators.required],
      headerValue: ['', Validators.required],
    });
  }

  onHttpHeadersAddClick() {
    this.all.controls.httpHeaders.push(this.buildHeaderFormGroup());
  }

  onHttpHeadersRemoveClick(index: number) {
    this.all.controls.httpHeaders.removeAt(index);
  }
}
