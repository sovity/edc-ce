import {Injectable} from '@angular/core';
import {Constraint, Permission, PolicyDefinition} from '../../edc-dmgmt-client';
import {NewPolicyDialogFormValue} from '../components/new-policy-dialog/new-policy-dialog-form-model';

@Injectable({
  providedIn: 'root',
})
export class PolicyDefinitionBuilder {
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
        return [this.buildTimePeriodRestrictionPermission(formValue)];
      case 'Connector-Restricted-Usage':
        return [this.buildConnectorRestrictedUsagePermission(formValue)];
      default:
        throw new Error(`Unknown policyType: ${policyType}`);
    }
  }

  private buildConnectorRestrictedUsagePermission(
    formValue: NewPolicyDialogFormValue,
  ) {
    return {
      edctype: 'dataspaceconnector:permission',
      id: null,
      action: {type: 'USE'},
      constraints: [
        {
          edctype: 'AtomicConstraint',
          leftExpression: {
            edctype: 'dataspaceconnector:literalexpression',
            value: 'REFERRING_CONNECTOR',
          },
          operator: 'EQ',
          rightExpression: {
            edctype: 'dataspaceconnector:literalexpression',
            value: formValue.connectorId,
          },
        } as Constraint,
      ],
      duties: [],
    } as Permission;
  }

  private buildTimePeriodRestrictionPermission(
    formValue: NewPolicyDialogFormValue,
  ) {
    return {
      edctype: 'dataspaceconnector:permission',
      id: null,
      action: {type: 'USE'},
      constraints: [
        {
          edctype: 'AtomicConstraint',
          leftExpression: {
            edctype: 'dataspaceconnector:literalexpression',
            value: 'POLICY_EVALUATION_TIME',
          },
          operator: 'GT',
          rightExpression: {
            edctype: 'dataspaceconnector:literalexpression',
            value: formValue.range?.start?.toISOString()!,
          },
        } as Constraint,
        {
          edctype: 'AtomicConstraint',
          leftExpression: {
            edctype: 'dataspaceconnector:literalexpression',
            value: 'POLICY_EVALUATION_TIME',
          },
          operator: 'LT',
          rightExpression: {
            edctype: 'dataspaceconnector:literalexpression',
            value: formValue.range?.end?.toISOString()!,
          },
        },
      ],
      duties: [],
    } as Permission;
  }
}
