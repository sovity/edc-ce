import {
  FormArray,
  FormControl,
  FormGroup,
  ɵFormGroupValue,
} from '@angular/forms';
import {DataAddressType} from '../../../../component-library/data-address/data-address-type-select/data-address-type';
import {HttpDatasourceQueryParamFormModel} from '../../asset-page/asset-create-dialog/model/http-datasource-query-param-form-model';
import {HttpDatasinkAuthHeaderType} from './http-datasink-auth-header-type';
import {HttpDatasinkHeaderFormModel} from './http-datasink-header-form-model';

/**
 * Form Value Type
 */
export type ContractAgreementTransferDialogFormValue =
  ɵFormGroupValue<ContractAgreementTransferDialogFormModel>;

/**
 * Form Group Template Type
 */
export interface ContractAgreementTransferDialogFormModel {
  dataAddressType: FormControl<DataAddressType>;

  // Custom Datasink JSON
  dataDestination: FormControl<string>;

  // Custom Transfer Process Request JSON
  transferProcessRequest: FormControl<string>;

  // Http Datasink
  httpUrl: FormControl<string>;
  httpMethod: FormControl<string>;

  httpAuthHeaderType: FormControl<HttpDatasinkAuthHeaderType>;
  httpAuthHeaderName: FormControl<string>;
  httpAuthHeaderValue: FormControl<string>;
  httpAuthHeaderSecretName: FormControl<string>;
  httpHeaders: FormArray<FormGroup<HttpDatasinkHeaderFormModel>>;

  // Http Datasource Parameterization
  httpProxiedPath: FormControl<string>;
  httpProxiedMethod: FormControl<string>;
  httpProxiedQueryParams: FormArray<
    FormGroup<HttpDatasourceQueryParamFormModel>
  >;
  httpProxiedBody: FormControl<string>;
  httpProxiedBodyContentType: FormControl<string>;
}
