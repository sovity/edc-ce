import {FormControl, ɵFormGroupValue} from '@angular/forms';

/**
 * Form Model for Edit Asset Form > Datasource > HTTP/REST > Header
 */
export interface HttpDatasourceHeaderFormModel {
  headerName: FormControl<string>;
  headerValue: FormControl<string>;
}

/**
 * Form Value for Edit Asset Form > Datasource > HTTP/REST > Header
 */
export type HttpDatasourceHeaderFormValue =
  ɵFormGroupValue<HttpDatasourceHeaderFormModel>;
