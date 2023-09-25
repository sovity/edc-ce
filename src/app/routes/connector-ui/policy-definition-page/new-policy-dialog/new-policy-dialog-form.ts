import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {switchDisabledControls} from '../../../../core/utils/form-group-utils';
import {dateRangeRequired} from '../../../../core/validators/date-range-required';
import {noWhitespacesOrColonsValidator} from '../../../../core/validators/no-whitespaces-or-colons-validator';
import {
  NewPolicyDialogFormModel,
  NewPolicyDialogFormValue,
  PolicyType,
} from './new-policy-dialog-form-model';

/**
 * Handles AngularForms for NewPolicyDialog
 */
@Injectable()
export class NewPolicyDialogForm {
  group = this.buildFormGroup();

  /**
   * Quick access to full value
   */
  get value(): NewPolicyDialogFormValue {
    return this.group.value;
  }

  get policyType(): PolicyType {
    return this.group.controls.policyType.value;
  }

  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(): FormGroup<NewPolicyDialogFormModel> {
    const newPolicyFormGroup: FormGroup<NewPolicyDialogFormModel> =
      this.formBuilder.nonNullable.group({
        id: ['', [Validators.required, noWhitespacesOrColonsValidator]],
        policyType: [
          'Connector-Restricted-Usage' as PolicyType,
          Validators.required,
        ],
        range: this.formBuilder.group(
          {
            start: null as Date | null,
            end: null as Date | null,
          },
          {validators: dateRangeRequired},
        ),
        connectorId: ['', Validators.required],
      });

    switchDisabledControls<NewPolicyDialogFormValue>(
      newPolicyFormGroup,
      (value) => {
        const timePeriodRestricted =
          value.policyType === 'Time-Period-Restricted';
        const connecterRestrictedUsage =
          value.policyType === 'Connector-Restricted-Usage';

        return {
          id: true,
          policyType: true,
          range: timePeriodRestricted,
          connectorId: connecterRestrictedUsage,
        };
      },
    );

    return newPolicyFormGroup;
  }
}
