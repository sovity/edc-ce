import {FormControl, ɵFormGroupValue} from '@angular/forms';

/**
 * Form Value Type
 */
export type ContractAgreementTransferDialogFormValue =
  ɵFormGroupValue<ContractAgreementTerminationDialogFormModel>;

/**
 * Form Group Template Type
 */
export interface ContractAgreementTerminationDialogFormModel {
  shortReason: FormControl<string>;
  detailedReason: FormControl<string>;
}
