import {FormControl, ɵFormGroupValue} from '@angular/forms';
import {DataAddressType} from '../data-address-type-select/data-address-type';

/**
 * Form Value Type
 */
export type ContractAgreementTransferDialogFormValue =
  ɵFormGroupValue<ContractAgreementTransferDialogFormModel>;

/**
 * Form Group Template Type
 */
export interface ContractAgreementTransferDialogFormModel {
  dataAddressType: FormControl<DataAddressType | null>;
  dataDestination: FormControl<string>;
  httpUrl: FormControl<string>;
}
