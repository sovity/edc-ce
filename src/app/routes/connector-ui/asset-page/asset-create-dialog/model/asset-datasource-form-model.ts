import {
  FormArray,
  FormControl,
  FormGroup,
  ɵFormGroupValue,
} from '@angular/forms';
import {DataAddressType} from '../../../../../component-library/data-address/data-address-type-select/data-address-type';
import {HttpDatasourceAuthHeaderType} from './http-datasource-auth-header-type';
import {HttpDatasourceHeaderFormModel} from './http-datasource-header-form-model';
import {HttpDatasourceQueryParamFormModel} from './http-datasource-query-param-form-model';

/**
 * Form Model for AssetEditorDialog > Datasource
 */
export interface AssetDatasourceFormModel {
  dataAddressType: FormControl<DataAddressType>;

  // Custom Datasource JSON
  dataDestination: FormControl<string>;

  // Http Datasource
  httpUrl: FormControl<string>;
  httpMethod: FormControl<string>;

  httpAuthHeaderType: FormControl<HttpDatasourceAuthHeaderType>;
  httpAuthHeaderName: FormControl<string>;
  httpAuthHeaderValue: FormControl<string>;
  httpAuthHeaderSecretName: FormControl<string>;
  httpHeaders: FormArray<FormGroup<HttpDatasourceHeaderFormModel>>;
  httpQueryParams: FormArray<FormGroup<HttpDatasourceQueryParamFormModel>>;
  httpProxyMethod: FormControl<boolean>;
  httpProxyPath: FormControl<boolean>;
  httpProxyQueryParams: FormControl<boolean>;
  httpProxyBody: FormControl<boolean>;
  httpDefaultPath: FormControl<string>;
}

/**
 * Form Value for AssetEditorDialog > Datasource
 */
export type AssetDatasourceFormValue =
  ɵFormGroupValue<AssetDatasourceFormModel>;
