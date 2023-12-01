import {FormControl, FormGroup, ɵFormGroupValue} from '@angular/forms';

/**
 * Form Value Type
 */
export type NewPolicyDialogFormValue =
  ɵFormGroupValue<NewPolicyDialogFormModel>;

/**
 * Form Group Template Type
 */
export interface NewPolicyDialogFormModel {
  id: FormControl<string>;
  policyType: FormControl<PolicyType>;
  range: FormGroup<{
    start: FormControl<Date | null>;
    end: FormControl<Date | null>;
  }>;
  participantIds: FormControl<string[]>;
}

export type PolicyType =
  | 'Time-Period-Restricted'
  | 'Connector-Restricted-Usage';
