import {Injectable} from '@angular/core';
import {addDays} from 'date-fns';
import {NewPolicyDialogFormValue} from '../../routes/connector-ui/policy-definition-page/new-policy-dialog/new-policy-dialog-form-model';
import {Permission, PolicyDefinition} from './api/legacy-managent-api-client';
import {
  AtomicConstraint,
  ExpressionLeftSideConstants,
  Operator,
} from './api/policy-type-ext';
import {PolicyDefinitionUtils} from './policy-definition-utils';

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
    if (formValue.rangeIsOpenEnded) {
      const start = formValue.rangeStart!!;
      return [
        this.policyDefinitionUtils.buildPermission({
          constraints: [this.evaluationTime('GEQ', start)],
        }),
      ];
    } else {
      const start = formValue.range!!.start!!;
      const end = addDays(formValue.range!!.end!!, 1);

      return [
        this.policyDefinitionUtils.buildPermission({
          constraints: [
            this.evaluationTime('GEQ', start),
            this.evaluationTime('LT', end),
          ],
        }),
      ];
    }
  }

  private evaluationTime(operator: Operator, date: Date): AtomicConstraint {
    return this.policyDefinitionUtils.buildAtomicConstraint(
      ExpressionLeftSideConstants.PolicyEvaluationTime,
      operator,
      date.toISOString()!,
    );
  }
}
