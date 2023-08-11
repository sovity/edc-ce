import {Injectable} from '@angular/core';
import {AssetDatasourceFormValue} from '../../routes/connector-ui/asset-page/asset-create-dialog/model/asset-datasource-form-model';
import {ContractAgreementTransferDialogFormValue} from '../../routes/connector-ui/contract-agreement-page/contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-form-model';
import {HttpRequestParamsMapper} from './http-params-mapper.service';

@Injectable({providedIn: 'root'})
export class DataAddressMapper {
  constructor(private httpRequestParamsMapper: HttpRequestParamsMapper) {}

  buildDataAddressProperties(
    formValue:
      | AssetDatasourceFormValue
      | ContractAgreementTransferDialogFormValue
      | undefined,
  ): Record<string, string> {
    switch (formValue?.dataAddressType) {
      case 'Custom-Data-Address-Json':
        return JSON.parse(formValue.dataDestination?.trim()!!);
      case 'Http':
        return this.httpRequestParamsMapper.buildHttpDataAddress(formValue);
      default:
        throw new Error(
          `Invalid Data Address Type ${formValue?.dataAddressType}`,
        );
    }
  }
}
