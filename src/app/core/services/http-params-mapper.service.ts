import {Injectable} from '@angular/core';
import {AssetDatasourceFormValue} from '../../routes/connector-ui/asset-page/asset-edit-dialog/form/model/asset-datasource-form-model';
import {HttpDatasourceHeaderFormValue} from '../../routes/connector-ui/asset-page/asset-edit-dialog/form/model/http-datasource-header-form-model';
import {HttpDatasourceQueryParamFormValue} from '../../routes/connector-ui/asset-page/asset-edit-dialog/form/model/http-datasource-query-param-form-model';
import {ContractAgreementTransferDialogFormValue} from '../../routes/connector-ui/contract-agreement-page/contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-form-model';
import {mapKeys, removeNullValues} from '../utils/record-utils';
import {everythingAfter, everythingBefore} from '../utils/string-utils';
import {DataAddressProperty} from './models/data-address-properties';
import {HttpDataAddressParams} from './models/http-data-address-params';
import {UiAssetMapped} from './models/ui-asset-mapped';

@Injectable({providedIn: 'root'})
export class HttpRequestParamsMapper {
  buildHttpDataAddress(
    formValue:
      | AssetDatasourceFormValue
      | ContractAgreementTransferDialogFormValue
      | undefined,
  ): Record<string, string> {
    const params = this.buildHttpRequestParams(formValue);
    return this.encodeHttpRequestParams(params);
  }

  encodeHttpProxyTransferRequestProperties(
    asset: UiAssetMapped,
    value: ContractAgreementTransferDialogFormValue,
  ): Record<string, string> {
    const method = value.httpProxiedMethod?.trim() ?? '';
    const pathSegments = this.getUrl(value.httpProxiedPath);
    const queryParams = this.getQueryParams(
      value.httpProxiedPath,
      value.httpProxiedQueryParams,
    );
    const body = value.httpProxiedBody?.trim() || null;
    const contentType = value.httpProxiedBodyContentType?.trim() || null;

    const proxyMethod =
      value.showAllHttpParameterizationFields ||
      asset.httpDatasourceHintsProxyMethod;
    const proxyPath =
      value.showAllHttpParameterizationFields ||
      asset.httpDatasourceHintsProxyPath;
    const proxyQueryParams =
      value.showAllHttpParameterizationFields ||
      asset.httpDatasourceHintsProxyQueryParams;
    const proxyBody =
      value.showAllHttpParameterizationFields ||
      asset.httpDatasourceHintsProxyBody;

    return removeNullValues({
      [DataAddressProperty.method]: proxyMethod ? method : null,
      [DataAddressProperty.pathSegments]: proxyPath ? pathSegments : null,
      [DataAddressProperty.queryParams]: proxyQueryParams ? queryParams : null,
      [DataAddressProperty.body]: proxyBody ? body : null,
      [DataAddressProperty.contentType]: proxyBody ? contentType : null,
    });
  }

  encodeHttpRequestParams(
    httpRequestParams: HttpDataAddressParams,
  ): Record<string, string> {
    const bool = (b?: boolean | null) => (b ? 'true' : null);

    const props: Record<string, string | null> = {
      [DataAddressProperty.type]: 'HttpData',
      [DataAddressProperty.baseUrl]: httpRequestParams.baseUrl,
      [DataAddressProperty.method]: httpRequestParams.method,
      [DataAddressProperty.authKey]: httpRequestParams.authHeaderName,
      [DataAddressProperty.authCode]: httpRequestParams.authHeaderValue,
      [DataAddressProperty.secretName]: httpRequestParams.authHeaderSecretName,
      [DataAddressProperty.proxyMethod]: bool(httpRequestParams.proxyMethod),
      [DataAddressProperty.proxyPath]: bool(httpRequestParams.proxyPath),
      [DataAddressProperty.proxyQueryParams]: bool(
        httpRequestParams.proxyQueryParams,
      ),
      [DataAddressProperty.proxyBody]: bool(httpRequestParams.proxyBody),
      [DataAddressProperty.queryParams]: httpRequestParams.queryParams,
      ...mapKeys(
        httpRequestParams.headers,
        (k) => `${DataAddressProperty.header}:${k}`,
      ),
    };
    return removeNullValues(props);
  }

  buildHttpRequestParams(
    formValue: AssetDatasourceFormValue | undefined,
  ): HttpDataAddressParams {
    const proxyMethod = !!formValue?.httpProxyMethod;
    const proxyPath = !!formValue?.httpProxyPath;
    const proxyQueryParams = !!formValue?.httpProxyQueryParams;
    const proxyBody = !!formValue?.httpProxyBody;

    const {authHeaderName, authHeaderValue, authHeaderSecretName} =
      this.getAuthFields(formValue);

    let method = formValue?.httpMethod?.trim().toUpperCase() || null;
    if (proxyMethod) {
      method = null;
    }

    const baseUrl = this.getUrl(formValue?.httpUrl);
    const queryParams = this.getRawQueryParams(
      formValue?.httpUrl,
      formValue?.httpQueryParams,
    );

    return {
      baseUrl: baseUrl!!,
      method,
      authHeaderName,
      authHeaderValue,
      authHeaderSecretName,
      proxyMethod,
      proxyPath,
      proxyQueryParams,
      proxyBody,
      queryParams,
      headers: this.buildHttpHeaders(formValue?.httpHeaders ?? []),
    };
  }

  private getAuthFields(formValue: AssetDatasourceFormValue | undefined): {
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
      authHeaderSecretName =
        formValue?.httpAuthHeaderSecretName?.trim() || null;
    }
    return {authHeaderName, authHeaderValue, authHeaderSecretName};
  }

  getUrl(rawUrl: string | null | undefined): string | null {
    return everythingBefore('?', this.trimOrEmpty(rawUrl));
  }

  getQueryParams(
    rawUrl: string | null | undefined,
    rawQueryParams: HttpDatasourceQueryParamFormValue[] | null | undefined,
  ): string | null {
    const queryParamSegments = (rawQueryParams ?? []).map((param) =>
      this.encodeQueryParam(param),
    );
    const queryParams = [
      everythingAfter('?', this.trimOrEmpty(rawUrl)),
      ...queryParamSegments,
    ]
      .filter((it) => !!it)
      .join('&');

    return queryParams;
  }

  getRawQueryParams(
    rawUrl: string | null | undefined,
    rawQueryParams: HttpDatasourceQueryParamFormValue[] | null | undefined,
  ): string | null {
    const queryParamSegments = (rawQueryParams ?? []).map((param) =>
      this.buildQueryParam(
        this.trimOrEmpty(param.paramName),
        this.trimOrEmpty(param.paramValue),
      ),
    );
    const queryParams = [
      everythingAfter('?', this.trimOrEmpty(rawUrl)),
      ...queryParamSegments,
    ]
      .filter((it) => !!it)
      .join('&');

    return queryParams;
  }

  private encodeQueryParam(param: HttpDatasourceQueryParamFormValue): string {
    const k = encodeURIComponent(this.trimOrEmpty(param.paramName));
    const v = encodeURIComponent(this.trimOrEmpty(param.paramValue));
    return this.buildQueryParam(k, v);
  }

  private trimOrEmpty(s: string | null | undefined): string {
    return s?.trim() ?? '';
  }

  private buildQueryParam(name: string, value: string) {
    return `${name}=${value}`;
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
