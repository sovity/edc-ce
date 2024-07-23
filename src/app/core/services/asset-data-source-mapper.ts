import {Injectable} from '@angular/core';
import {UiDataSource} from '@sovity.de/edc-client';
import {AssetDatasourceFormValue} from 'src/app/component-library/edit-asset-form/edit-asset-form/form/model/asset-datasource-form-model';
import {HttpDatasourceHeaderFormValue} from 'src/app/component-library/edit-asset-form/edit-asset-form/form/model/http-datasource-header-form-model';
import {getAuthFields} from '../utils/form-value-utils';
import {QueryParamsMapper} from './query-params-mapper';

@Injectable({providedIn: 'root'})
export class AssetDataSourceMapper {
  constructor(private queryParamsMapper: QueryParamsMapper) {}

  buildDataSourceOrNull(
    formValue: AssetDatasourceFormValue | undefined,
  ): UiDataSource | null {
    if (!formValue || formValue.dataSourceAvailability === 'Unchanged') {
      return null;
    }
    return this.buildDataSource(formValue);
  }

  buildDataSource(formValue: AssetDatasourceFormValue): UiDataSource {
    if (formValue.dataSourceAvailability === 'On-Request') {
      return this.buildOnRequestDataSource(formValue);
    }

    switch (formValue?.dataAddressType) {
      case 'Custom-Data-Address-Json':
        return this.buildCustomDataSource(formValue);
      case 'Http':
        return this.buildHttpDataSource(formValue);
      default:
        throw new Error(
          `Invalid Data Address Type ${formValue?.dataAddressType}`,
        );
    }
  }

  private buildCustomDataSource(
    formValue: AssetDatasourceFormValue,
  ): UiDataSource {
    const json = JSON.parse(formValue.dataDestination?.trim()!!);
    return {
      type: 'CUSTOM',
      customProperties: json,
    };
  }

  private buildHttpDataSource(
    formValue: AssetDatasourceFormValue,
  ): UiDataSource {
    const baseUrl = this.queryParamsMapper.getBaseUrlWithoutQueryParams(
      formValue.httpUrl!,
    )!;
    const queryString = this.queryParamsMapper.getFullQueryString(
      formValue.httpUrl!,
      formValue.httpQueryParams ?? [],
    );

    const authFields = getAuthFields(formValue);

    return {
      type: 'HTTP_DATA',
      httpData: {
        method: formValue.httpMethod,
        baseUrl,
        queryString: queryString ?? undefined,
        authHeaderName: authFields.authHeaderName ?? undefined,
        authHeaderValue: {
          secretName: authFields.authHeaderSecretName ?? undefined,
          rawValue: authFields.authHeaderValue ?? undefined,
        },
        headers: this.buildHttpHeaders(formValue.httpHeaders ?? []),
        enableMethodParameterization: formValue.httpProxyMethod,
        enablePathParameterization: formValue.httpProxyPath,
        enableQueryParameterization: formValue.httpProxyQueryParams,
        enableBodyParameterization: formValue.httpProxyBody,
      },
    };
  }

  private buildOnRequestDataSource(
    formValue: AssetDatasourceFormValue,
  ): UiDataSource {
    return {
      type: 'ON_REQUEST',
      onRequest: {
        contactEmail: formValue.contactEmail!!,
        contactPreferredEmailSubject: formValue.contactPreferredEmailSubject!!,
      },
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
