import {FormControl, ɵFormGroupValue} from '@angular/forms';

/**
 * Form Model for AssetEditorDialog > Datasource > HTTP/REST > Header
 */
export interface HttpDatasourceHeaderFormModel {
  headerName: FormControl<string>;
  headerValue: FormControl<string>;
}

/**
 * Form Value for AssetEditorDialog > Datasource > HTTP/REST > Header
 */
export type HttpDatasourceHeaderFormValue =
  ɵFormGroupValue<HttpDatasourceHeaderFormModel>;
