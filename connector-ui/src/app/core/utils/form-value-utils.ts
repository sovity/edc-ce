/*
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 * Copyright 2025 sovity GmbH
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
 *     Fraunhofer FIT - bugfix for missing auth header
 *     sovity - continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {ContractAgreementTransferDialogFormValue} from '../../routes/connector-ui/contract-agreement-page/contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-form-model';
import {AssetDatasourceFormValue} from '../../shared/business/edit-asset-form/form/model/asset-datasource-form-model';

export function getAuthFields(
  formValue:
    | AssetDatasourceFormValue
    | ContractAgreementTransferDialogFormValue
    | undefined,
): {
  authHeaderName: string | null;
  authHeaderValue: string | null;
  authHeaderSecretName: string | null;
} {
  let authHeaderName: string | null = null;
  if (formValue?.httpAuthHeaderType !== 'None') {
    authHeaderName = formValue?.httpAuthHeaderName?.trim() || null;
  }

  let authHeaderValue: string | null = null;
  if (authHeaderName && formValue?.httpAuthHeaderType === 'Value') {
    authHeaderValue = formValue?.httpAuthHeaderValue?.trim() || null;
  }

  let authHeaderSecretName: string | null = null;
  if (authHeaderName && formValue?.httpAuthHeaderType === 'Vault-Secret') {
    authHeaderSecretName = formValue?.httpAuthHeaderSecretName?.trim() || null;
  }
  return {authHeaderName, authHeaderValue, authHeaderSecretName};
}
