import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {validQueryParam} from 'src/app/core/validators/valid-query-param';
import {switchDisabledControls} from '../../../../../core/utils/form-group-utils';
import {jsonValidator} from '../../../../../core/validators/json-validator';
import {urlValidator} from '../../../../../core/validators/url-validator';
import {
  AssetDatasourceFormModel,
  AssetDatasourceFormValue,
} from './model/asset-datasource-form-model';
import {
  HttpDatasourceHeaderFormModel,
  HttpDatasourceHeaderFormValue,
} from './model/http-datasource-header-form-model';
import {
  HttpDatasourceQueryParamFormModel,
  HttpDatasourceQueryParamFormValue,
} from './model/http-datasource-query-param-form-model';

@Injectable()
export class AssetDatasourceFormBuilder {
  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(
    initial: AssetDatasourceFormValue,
  ): FormGroup<AssetDatasourceFormModel> {
    const datasource: FormGroup<AssetDatasourceFormModel> =
      this.formBuilder.nonNullable.group({
        dataAddressType: initial?.dataAddressType!,
        dataDestination: [
          initial?.dataDestination!,
          [Validators.required, jsonValidator],
        ],

        // Http Datasource Fields
        httpUrl: [initial?.httpUrl!, [Validators.required, urlValidator]],
        httpMethod: [initial?.httpMethod!, Validators.required],

        httpAuthHeaderType: [initial?.httpAuthHeaderType!],
        httpAuthHeaderName: [initial?.httpAuthHeaderName!, Validators.required],
        httpAuthHeaderValue: [
          initial?.httpAuthHeaderValue!,
          Validators.required,
        ],
        httpAuthHeaderSecretName: [
          initial?.httpAuthHeaderSecretName!,
          Validators.required,
        ],
        httpQueryParams: this.formBuilder.array(
          initial?.httpQueryParams?.map(
            (param: HttpDatasourceQueryParamFormValue) =>
              this.buildQueryParamFormGroup(param),
          ) ?? [],
        ),

        httpDefaultPath: [initial?.httpDefaultPath!],
        httpProxyMethod: [initial?.httpProxyMethod!],
        httpProxyPath: [initial?.httpProxyPath!],
        httpProxyQueryParams: [initial?.httpProxyQueryParams!],
        httpProxyBody: [initial?.httpProxyBody!],

        httpHeaders: this.formBuilder.array(
          initial?.httpHeaders?.map((header: HttpDatasourceHeaderFormValue) =>
            this.buildHeaderFormGroup(header),
          ) ?? [],
        ),
      });

    switchDisabledControls<AssetDatasourceFormValue>(datasource, (value) => {
      const customDataAddressJson =
        value.dataAddressType === 'Custom-Data-Address-Json';

      const http = value.dataAddressType === 'Http';
      const httpAuth = value.httpAuthHeaderType !== 'None';
      const httpAuthByValue = value.httpAuthHeaderType === 'Value';
      const httpAuthByVault = value.httpAuthHeaderType === 'Vault-Secret';
      const proxyPath = !!value.httpProxyPath;

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

  buildHeaderFormGroup(
    initial: HttpDatasourceHeaderFormValue,
  ): FormGroup<HttpDatasourceHeaderFormModel> {
    return this.formBuilder.nonNullable.group({
      headerName: [initial.headerName!, Validators.required],
      headerValue: [initial.headerValue!, Validators.required],
    });
  }

  buildQueryParamFormGroup(
    initial: HttpDatasourceQueryParamFormValue,
  ): FormGroup<HttpDatasourceQueryParamFormModel> {
    return this.formBuilder.nonNullable.group({
      paramName: [initial.paramName!, [Validators.required, validQueryParam]],
      paramValue: [initial.paramValue!, [validQueryParam]],
    });
  }
}
