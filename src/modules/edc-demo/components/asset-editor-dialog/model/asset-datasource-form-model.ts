import {
  FormArray,
  FormControl,
  FormGroup,
  ɵFormGroupValue,
} from '@angular/forms';
import {DataAddressType} from '../../data-address-type-select/data-address-type';
import {HttpDatasourceAuthHeaderType} from './http-datasource-auth-header-type';
import {HttpDatasourceHeaderFormModel} from './http-datasource-header-form-model';

/**
 * Form Model for AssetEditorDialog > Datasource
 */
export interface AssetDatasourceFormModel {
  dataAddressType: FormControl<DataAddressType>;
  publisher: FormControl<string>;
  standardLicense: FormControl<string>;
  endpointDocumentation: FormControl<string>;

  // Custom Datasource JSON
  dataDestination: FormControl<string>;

  // Http Datasource
  httpUrl: FormControl<string>;
  httpMethod: FormControl<string>;

  httpRequestBodyEnabled: FormControl<boolean>;
  httpRequestBodyValue: FormControl<string>;
  httpContentType: FormControl<string>;

  httpAuthHeaderType: FormControl<HttpDatasourceAuthHeaderType>;
  httpAuthHeaderName: FormControl<string>;
  httpAuthHeaderValue: FormControl<string>;
  httpAuthHeaderSecretName: FormControl<string>;
  httpHeaders: FormArray<FormGroup<HttpDatasourceHeaderFormModel>>;
}

/**
 * Form Value for AssetEditorDialog > Datasource
 */
export type AssetDatasourceFormValue =
  ɵFormGroupValue<AssetDatasourceFormModel>;
