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
import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {switchDisabledControls} from '../../../../core/utils/form-group-utils';
import {jsonValidator} from '../../../../core/validators/json-validator';
import {urlValidator} from '../../../../core/validators/url-validator';
import {HttpDatasourceAuthHeaderType} from '../../../../shared/business/edit-asset-form/form/model/http-datasource-auth-header-type';
import {HttpDatasourceQueryParamFormModel} from '../../../../shared/business/edit-asset-form/form/model/http-datasource-query-param-form-model';
import {DataAddressType} from '../../../../shared/form-elements/data-address-type-select/data-address-type';
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
}
