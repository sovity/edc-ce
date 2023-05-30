import {FormControl, ɵFormGroupValue} from '@angular/forms';

/**
 * Form Model for ContractAgreementTransferDialog > Datasink > HTTP/REST > Header
 */
export interface HttpDatasinkHeaderFormModel {
  headerName: FormControl<string>;
  headerValue: FormControl<string>;
}

/**
 * Form Value for ContractAgreementTransferDialog > Datasink > HTTP/REST > Header
 */
export type HttpDatasinkHeaderFormValue =
  ɵFormGroupValue<HttpDatasinkHeaderFormModel>;
