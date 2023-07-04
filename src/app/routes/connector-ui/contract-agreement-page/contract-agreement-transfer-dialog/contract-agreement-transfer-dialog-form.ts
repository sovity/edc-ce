import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DataAddressType} from '../../../../component-library/data-address/data-address-type-select/data-address-type';
import {switchDisabledControls} from '../../../../core/utils/form-group-utils';
import {jsonValidator} from '../../../../core/validators/json-validator';
import {urlValidator} from '../../../../core/validators/url-validator';
import {HttpDatasourceAuthHeaderType} from '../../asset-page/asset-create-dialog/model/http-datasource-auth-header-type';
import {HttpDatasourceQueryParamFormModel} from '../../asset-page/asset-create-dialog/model/http-datasource-query-param-form-model';
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
        transferProcessRequest: ['', [Validators.required, jsonValidator]],

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

        httpProxiedPath: [''],
        httpProxiedMethod: ['GET'],
        httpProxiedQueryParams: this.formBuilder.array(
          new Array<FormGroup<HttpDatasourceQueryParamFormModel>>(),
        ),
        httpProxiedBody: [''],
        httpProxiedBodyContentType: [''],
      });

    switchDisabledControls<ContractAgreementTransferDialogFormValue>(
      all,
      (value) => {
        const customDataAddressJson =
          value.dataAddressType === 'Custom-Data-Address-Json';

        const customTransferProcessRequest =
          value.dataAddressType === 'Custom-Transfer-Process-Request';

        const http = value.dataAddressType === 'Http';
        const httpAuth = value.httpAuthHeaderType !== 'None';
        const httpAuthByValue = value.httpAuthHeaderType === 'Value';
        const httpAuthByVault = value.httpAuthHeaderType === 'Vault-Secret';

        return {
          dataAddressType: true,

          // Custom Datasink JSON
          dataDestination: customDataAddressJson,
          transferProcessRequest: customTransferProcessRequest,

          // Http Datasink Fields
          httpUrl: http,
          httpMethod: http,

          httpAuthHeaderType: http,
          httpAuthHeaderName: http && httpAuth,
          httpAuthHeaderValue: http && httpAuthByValue,
          httpAuthHeaderSecretName: http && httpAuthByVault,

          httpHeaders: http,

          httpProxiedPath: !customTransferProcessRequest,
          httpProxiedMethod: !customTransferProcessRequest,
          httpProxiedQueryParams: !customTransferProcessRequest,
          httpProxiedBody: !customTransferProcessRequest,
          httpProxiedBodyContentType: !customTransferProcessRequest,
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

  buildQueryParamFormGroup(): FormGroup<HttpDatasourceQueryParamFormModel> {
    return this.formBuilder.nonNullable.group({
      paramName: ['', Validators.required],
      paramValue: [''],
    });
  }

  onHttpHeadersAddClick() {
    this.all.controls.httpHeaders.push(this.buildHeaderFormGroup());
  }

  onHttpHeadersRemoveClick(index: number) {
    this.all.controls.httpHeaders.removeAt(index);
  }

  onHttpQueryParamsAddClick() {
    this.all.controls.httpProxiedQueryParams.push(
      this.buildQueryParamFormGroup(),
    );
  }

  onHttpQueryParamsRemoveClick(index: number) {
    this.all.controls.httpProxiedQueryParams.removeAt(index);
  }
}
