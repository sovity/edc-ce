import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {switchDisabledControls} from '../../../utils/form-group-utils';
import {jsonValidator} from '../../../validators/json-validator';
import {urlValidator} from '../../../validators/url-validator';
import {DataAddressType} from '../../data-address-type-select/data-address-type';
import {
  AssetDatasourceFormModel,
  AssetDatasourceFormValue,
} from '../model/asset-datasource-form-model';
import {HttpDatasourceAuthHeaderType} from './http-datasource-auth-header-type';
import {HttpDatasourceHeaderFormModel} from './http-datasource-header-form-model';

@Injectable()
export class AssetDatasourceFormBuilder {
  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(): FormGroup<AssetDatasourceFormModel> {
    const datasource: FormGroup<AssetDatasourceFormModel> =
      this.formBuilder.nonNullable.group({
        dataAddressType: 'Http' as DataAddressType,
        dataDestination: ['', [Validators.required, jsonValidator]],
        publisher: ['', urlValidator],
        standardLicense: ['', urlValidator],
        endpointDocumentation: ['', urlValidator],

        // Http Datasource Fields
        httpUrl: ['', [Validators.required, urlValidator]],
        httpMethod: ['GET', Validators.required],

        httpAuthHeaderType: ['None' as HttpDatasourceAuthHeaderType],
        httpAuthHeaderName: ['', Validators.required],
        httpAuthHeaderValue: ['', Validators.required],
        httpAuthHeaderSecretName: ['', Validators.required],

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
}
