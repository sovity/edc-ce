import {FormControl, ɵFormGroupValue} from '@angular/forms';

/**
 * Form Model for AssetCreateDialog > Datasource > HTTP/REST > Header
 */
export interface HttpDatasourceHeaderFormModel {
  headerName: FormControl<string>;
  headerValue: FormControl<string>;
}

/**
 * Form Value for AssetCreateDialog > Datasource > HTTP/REST > Header
 */
export type HttpDatasourceHeaderFormValue =
  ɵFormGroupValue<HttpDatasourceHeaderFormModel>;
