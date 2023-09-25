import {Injectable} from '@angular/core';
import {AssetDatasourceFormValue} from '../../routes/connector-ui/asset-page/asset-create-dialog/model/asset-datasource-form-model';
import {HttpDatasourceHeaderFormValue} from '../../routes/connector-ui/asset-page/asset-create-dialog/model/http-datasource-header-form-model';
import {HttpDatasourceQueryParamFormValue} from '../../routes/connector-ui/asset-page/asset-create-dialog/model/http-datasource-query-param-form-model';
import {ContractAgreementTransferDialogFormValue} from '../../routes/connector-ui/contract-agreement-page/contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-form-model';
import {mapKeys, removeNullValues} from '../utils/record-utils';
import {everythingAfter, everythingBefore} from '../utils/string-utils';
import {DataAddressProperty} from './data-address-properties';
import {Asset} from './models/asset';
import {HttpRequestParams} from './models/http-request-params';

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
    asset: Asset,
    value: ContractAgreementTransferDialogFormValue,
  ): Record<string, string> {
    const method = value.httpProxiedMethod?.trim() ?? '';
    const {url: pathSegments, queryParams} = this.getUrlAndQueryParams(
      value.httpProxiedPath,
      value.httpProxiedQueryParams,
    );
    const body = value.httpProxiedBody?.trim() || null;
    const contentType = value.httpProxiedBodyContentType?.trim() || null;

    let proxyMethod =
      value.showAllHttpParameterizationFields ||
      asset.httpDatasourceHintsProxyMethod;
    let proxyPath =
      value.showAllHttpParameterizationFields ||
      asset.httpDatasourceHintsProxyPath;
    let proxyQueryParams =
      value.showAllHttpParameterizationFields ||
      asset.httpDatasourceHintsProxyQueryParams;
    let proxyBody =
      value.showAllHttpParameterizationFields ||
      asset.httpDatasourceHintsProxyBody;

    return removeNullValues({
      [DataAddressProperty.method]: proxyMethod ? method : null,
      [DataAddressProperty.pathSegments]: proxyPath ? pathSegments : null,
      [DataAddressProperty.queryParams]: proxyQueryParams ? queryParams : null,
      [DataAddressProperty.body]: proxyBody ? body : null,
      [DataAddressProperty.mediaType]: proxyBody ? contentType : null,
    });
  }

  encodeHttpRequestParams(
    httpRequestParams: HttpRequestParams,
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
  ): HttpRequestParams {
    let proxyMethod = !!formValue?.httpProxyMethod;
    let proxyPath = !!formValue?.httpProxyPath;
    let proxyQueryParams = !!formValue?.httpProxyQueryParams;
    let proxyBody = !!formValue?.httpProxyBody;

    let {authHeaderName, authHeaderValue, authHeaderSecretName} =
      this.getAuthFields(formValue);

    let method = formValue?.httpMethod?.trim().toUpperCase() || null;
    if (proxyMethod) {
      method = null;
    }

    let {url: baseUrl, queryParams} = this.getUrlAndQueryParams(
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

  getUrlAndQueryParams(
    rawUrl: string | null | undefined,
    rawQueryParams: HttpDatasourceQueryParamFormValue[] | null | undefined,
  ): {
    url: string | null;
    queryParams: string | null;
  } {
    let rawUrlTrimmed = rawUrl?.trim() ?? '';

    let url = everythingBefore('?', rawUrlTrimmed);

    let queryParamSegments = (rawQueryParams ?? []).map((param) =>
      this.encodeQueryParam(param),
    );
    let queryParams = [
      everythingAfter('?', rawUrlTrimmed),
      ...queryParamSegments,
    ]
      .filter((it) => !!it)
      .join('&');

    return {url: url || null, queryParams: queryParams || null};
  }

  private encodeQueryParam(param: HttpDatasourceQueryParamFormValue): string {
    let k = param.paramName?.trim() ?? '';
    let v = param.paramValue?.trim() ?? '';
    return `${encodeURIComponent(k)}=${encodeURIComponent(v)}`;
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
