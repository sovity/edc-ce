import {FormControl, ɵFormGroupValue} from '@angular/forms';

/**
 * Form Model for AssetEditorDialog > Datasource > HTTP/REST > Header
 */
export interface HttpDatasourceQueryParamFormModel {
  paramName: FormControl<string>;
  paramValue: FormControl<string>;
}

/**
 * Form Value for AssetEditorDialog > Datasource > HTTP/REST > QueryParam
 */
export type HttpDatasourceQueryParamFormValue =
  ɵFormGroupValue<HttpDatasourceQueryParamFormModel>;
