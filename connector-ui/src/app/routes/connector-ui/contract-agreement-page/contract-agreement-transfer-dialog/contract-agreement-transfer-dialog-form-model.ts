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
import {
  FormArray,
  FormControl,
  FormGroup,
  ɵFormGroupValue,
} from '@angular/forms';
import {DataAddressType} from '../../../../shared/form-elements/data-address-type-select/data-address-type';
import {HttpDatasinkAuthHeaderType} from './http-datasink-auth-header-type';
import {HttpDatasinkHeaderFormModel} from './http-datasink-header-form-model';

/**
 * Form Value Type
 */
export type ContractAgreementTransferDialogFormValue =
  ɵFormGroupValue<ContractAgreementTransferDialogFormModel>;

/**
 * Form Group Template Type
 */
export interface ContractAgreementTransferDialogFormModel {
  dataAddressType: FormControl<DataAddressType>;

  // Custom Datasink JSON
  dataDestination: FormControl<string>;

  // Custom Transfer Process Request JSON
  transferProcessRequest: FormControl<string>;

  // Http Datasink
  httpUrl: FormControl<string>;
  httpMethod: FormControl<string>;

  httpAuthHeaderType: FormControl<HttpDatasinkAuthHeaderType>;
  httpAuthHeaderName: FormControl<string>;
  httpAuthHeaderValue: FormControl<string>;
  httpAuthHeaderSecretName: FormControl<string>;
  httpHeaders: FormArray<FormGroup<HttpDatasinkHeaderFormModel>>;
}
