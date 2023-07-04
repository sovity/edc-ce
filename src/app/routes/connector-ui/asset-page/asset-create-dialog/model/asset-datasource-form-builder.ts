import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DataAddressType} from '../../../../../component-library/data-address/data-address-type-select/data-address-type';
import {switchDisabledControls} from '../../../../../core/utils/form-group-utils';
import {jsonValidator} from '../../../../../core/validators/json-validator';
import {urlValidator} from '../../../../../core/validators/url-validator';
import {
  AssetDatasourceFormModel,
  AssetDatasourceFormValue,
} from './asset-datasource-form-model';
import {HttpDatasourceAuthHeaderType} from './http-datasource-auth-header-type';
import {HttpDatasourceHeaderFormModel} from './http-datasource-header-form-model';
import {HttpDatasourceQueryParamFormModel} from './http-datasource-query-param-form-model';

@Injectable()
export class AssetDatasourceFormBuilder {
  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(): FormGroup<AssetDatasourceFormModel> {
    const datasource: FormGroup<AssetDatasourceFormModel> =
      this.formBuilder.nonNullable.group({
        dataAddressType: 'Http' as DataAddressType,
        dataDestination: ['', [Validators.required, jsonValidator]],

        // Http Datasource Fields
        httpUrl: ['', [Validators.required, urlValidator]],
        httpMethod: ['GET', Validators.required],

        httpAuthHeaderType: ['None' as HttpDatasourceAuthHeaderType],
        httpAuthHeaderName: ['', Validators.required],
        httpAuthHeaderValue: ['', Validators.required],
        httpAuthHeaderSecretName: ['', Validators.required],
        httpQueryParams: this.formBuilder.array(
          new Array<FormGroup<HttpDatasourceQueryParamFormModel>>(),
        ),

        httpDefaultPath: [''],
        httpProxyMethod: [false],
        httpProxyPath: [false],
        httpProxyQueryParams: [false],
        httpProxyBody: [false],

        httpHeaders: this.formBuilder.array(
          new Array<FormGroup<HttpDatasourceHeaderFormModel>>(),
        ),
      });

    switchDisabledControls<AssetDatasourceFormValue>(datasource, (value) => {
      const customDataAddressJson =
        value.dataAddressType === 'Custom-Data-Address-Json';

      const http = value.dataAddressType === 'Http';
      const httpAuth = value.httpAuthHeaderType !== 'None';
      const httpAuthByValue = value.httpAuthHeaderType === 'Value';
      const httpAuthByVault = value.httpAuthHeaderType === 'Vault-Secret';
      let proxyPath = !!value.httpProxyPath;

      return {
        dataAddressType: true,
        publisher: true,
        standardLicense: true,
        endpointDocumentation: true,

        // Custom Datasource JSON
        dataDestination: customDataAddressJson,

        // Http Datasource Fields
        httpUrl: http,
        httpMethod: http,

        httpAuthHeaderType: http,
        httpAuthHeaderName: http && httpAuth,
        httpAuthHeaderValue: http && httpAuthByValue,
        httpAuthHeaderSecretName: http && httpAuthByVault,
        httpQueryParams: http,

        httpDefaultPath: http && proxyPath,
        httpProxyMethod: http,
        httpProxyPath: http,
        httpProxyQueryParams: http,
        httpProxyBody: http,

        httpHeaders: http,
      };
    });

    return datasource;
  }

  buildHeaderFormGroup(): FormGroup<HttpDatasourceHeaderFormModel> {
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
}
