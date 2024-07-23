import {AssetDatasourceFormValue} from 'src/app/component-library/edit-asset-form/edit-asset-form/form/model/asset-datasource-form-model';
import {ContractAgreementTransferDialogFormValue} from '../../routes/connector-ui/contract-agreement-page/contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-form-model';

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
