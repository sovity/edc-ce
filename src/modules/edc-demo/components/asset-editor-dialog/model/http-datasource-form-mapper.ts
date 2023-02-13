import {Injectable} from '@angular/core';
import {HttpDatasourceProperties} from '../../../models/http-datasource-properties';
import {AssetDatasourceFormValue} from './asset-datasource-form-model';
import {HttpDatasourceHeaderFormValue} from './http-datasource-header-form-model';

@Injectable({providedIn: 'root'})
export class HttpDatasourceFormMapper {
  buildHttpDatasourceProperties(
    formValue: AssetDatasourceFormValue | undefined,
  ): HttpDatasourceProperties {
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
