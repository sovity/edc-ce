import {Injectable} from '@angular/core';
import {addDays} from 'date-fns';
import {Permission, PolicyDefinition} from '../../edc-dmgmt-client';
import {NewPolicyDialogFormValue} from '../components/new-policy-dialog/new-policy-dialog-form-model';
import {PolicyDefinitionUtils} from './policy-definition-utils';
import {ExpressionLeftSideConstants} from './policy-type-ext';

@Injectable({
  providedIn: 'root',
})
export class PolicyDefinitionBuilder {
  constructor(private policyDefinitionUtils: PolicyDefinitionUtils) {}

  /**
   * Build {@link PolicyDefinition} from {@link NewPolicyDialogFormValue}
   *
   * @param formValue {@link NewPolicyDialogFormValue}
   */
  buildPolicyDefinition(formValue: NewPolicyDialogFormValue): PolicyDefinition {
    return {
      id: formValue.id?.trim()!,
      policy: {
        permissions: this.buildPolicyPermissions(formValue),
      },
    };
  }

  private buildPolicyPermissions(
    formValue: NewPolicyDialogFormValue,
  ): Permission[] {
    let policyType = formValue.policyType;
    switch (policyType) {
      case 'Time-Period-Restricted':
        return this.buildTimePeriodRestrictionPermissions(formValue);
      case 'Connector-Restricted-Usage':
        return this.buildConnectorRestrictedUsagePermissions(formValue);
      default:
        throw new Error(`Unknown policyType: ${policyType}`);
    }
  }

  private buildConnectorRestrictedUsagePermissions(
    formValue: NewPolicyDialogFormValue,
  ): Permission[] {
    return [
      this.policyDefinitionUtils.buildPermission({
        constraints: [
          this.policyDefinitionUtils.buildAtomicConstraint(
            'REFERRING_CONNECTOR',
            'EQ',
            formValue.connectorId!,
          ),
        ],
      }),
    ];
  }

  private buildTimePeriodRestrictionPermissions(
    formValue: NewPolicyDialogFormValue,
  ): Permission[] {
    const start = formValue.range!!.start!!;
    const end = addDays(formValue.range!!.end!!, 1);

    return [
      this.policyDefinitionUtils.buildPermission({
        constraints: [
          this.policyDefinitionUtils.buildAtomicConstraint(
            ExpressionLeftSideConstants.PolicyEvaluationTime,
            'GEQ',
            start.toISOString()!,
          ),
          this.policyDefinitionUtils.buildAtomicConstraint(
            ExpressionLeftSideConstants.PolicyEvaluationTime,
            'LT',
            end.toISOString()!,
          ),
        ],
      }),
    ];
  }
}
