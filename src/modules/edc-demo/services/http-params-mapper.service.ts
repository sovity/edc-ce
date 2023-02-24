import {Injectable} from '@angular/core';
import {DataAddressDto} from '../../edc-dmgmt-client';
import {AssetDatasourceFormValue} from '../components/asset-editor-dialog/model/asset-datasource-form-model';
import {HttpDatasourceHeaderFormValue} from '../components/asset-editor-dialog/model/http-datasource-header-form-model';
import {ContractAgreementTransferDialogFormValue} from '../components/contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-form-model';
import {HttpRequestParams} from '../models/http-request-params';
import {removeNullValues} from '../utils/record-utils';

@Injectable({providedIn: 'root'})
export class HttpRequestParamsMapper {
  buildHttpDataAddressDto(
    formValue:
      | AssetDatasourceFormValue
      | ContractAgreementTransferDialogFormValue
      | undefined,
  ): DataAddressDto {
    const params = this.buildHttpRequestParams(formValue);
    return {
      properties: this.encodeHttpRequestParams(params),
    };
  }

  encodeHttpRequestParams(
    httpRequestParams: HttpRequestParams,
  ): Record<string, string> {
    const props: Record<string, string | null> = {
      type: 'HttpData',
      baseUrl: httpRequestParams.url,
      method: httpRequestParams.method,
      body: httpRequestParams.body,
      contentType: httpRequestParams.contentType,
      authKey: httpRequestParams.authHeaderName,
      authCode: httpRequestParams.authHeaderValue,
      secretName: httpRequestParams.authHeaderSecretName,
      ...Object.fromEntries(
        Object.entries(httpRequestParams.headers).map(
          ([headerName, headerValue]) => [`header:${headerName}`, headerValue],
        ),
      ),
    };
    return removeNullValues(props);
  }

  buildHttpRequestParams(
    formValue:
      | AssetDatasourceFormValue
      | ContractAgreementTransferDialogFormValue
      | undefined,
  ): HttpRequestParams {
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
      authHeaderSecretName =
        formValue?.httpAuthHeaderSecretName?.trim() || null;
    }

    return {
      url: formValue?.httpUrl?.trim() ?? '',
      method: formValue?.httpMethod?.trim().toUpperCase() ?? '',
      authHeaderName,
      authHeaderValue,
      authHeaderSecretName,
      body: formValue?.httpRequestBodyValue || null,
      contentType: formValue?.httpContentType || null,
      headers: this.buildHttpHeaders(formValue?.httpHeaders ?? []),
    };
  }

  private buildHttpHeaders(
    headers: HttpDatasourceHeaderFormValue[],
  ): Record<string, string> {
    return Object.fromEntries(
      headers
        .map((header) => [
          header.headerName?.trim() || '',
          header.headerValue?.trim() || '',
        ])
        .filter((a) => a[0] && a[1]),
    );
  }
}
