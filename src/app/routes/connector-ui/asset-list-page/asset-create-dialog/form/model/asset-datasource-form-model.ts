import {
  FormArray,
  FormControl,
  FormGroup,
  ɵFormGroupValue,
} from '@angular/forms';
import {UiDataSourceHttpDataMethod} from '@sovity.de/edc-client';
import {DataAddressType} from '../../../../../../shared/form-elements/data-address-type-select/data-address-type';
import {HttpDatasourceAuthHeaderType} from './http-datasource-auth-header-type';
import {HttpDatasourceHeaderFormModel} from './http-datasource-header-form-model';
import {HttpDatasourceQueryParamFormModel} from './http-datasource-query-param-form-model';

/**
 * Form Model for AssetCreateDialog > Datasource
 */
export interface AssetDatasourceFormModel {
  dataAddressType: FormControl<DataAddressType>;

  // Custom Datasource JSON
  dataDestination: FormControl<string>;

  // On-Request Datasource
  contactEmail: FormControl<string>;
  contactPreferredEmailSubject: FormControl<string>;

  // Http Datasource
  httpUrl: FormControl<string>;
  httpMethod: FormControl<UiDataSourceHttpDataMethod>;

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
 * Form Value for AssetCreateDialog > Datasource
 */
export type AssetDatasourceFormValue =
  ɵFormGroupValue<AssetDatasourceFormModel>;
