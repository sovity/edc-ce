import {Injectable} from '@angular/core';
import {
  OperatorDto,
  PolicyDefinitionCreateRequest,
  UiPolicyConstraint,
} from '@sovity.de/edc-client';
import {addDays} from 'date-fns';
import {NewPolicyDialogFormValue} from '../../routes/connector-ui/policy-definition-page/new-policy-dialog/new-policy-dialog-form-model';
import {PolicyLeftExpressions} from './api/model/policy-type-ext';

@Injectable({
  providedIn: 'root',
})
export class PolicyDefinitionBuilder {
  /**
   * Build {@link PolicyDefinitionCreateRequest} from {@link NewPolicyDialogFormValue}
   *
   * @param formValue {@link NewPolicyDialogFormValue}
   */
  buildPolicyDefinition(
    formValue: NewPolicyDialogFormValue,
  ): PolicyDefinitionCreateRequest {
    return {
      policyDefinitionId: formValue.id?.trim()!,
      policy: {
        constraints: this.buildPolicyConstraints(formValue),
      },
    };
  }

  private buildPolicyConstraints(
    formValue: NewPolicyDialogFormValue,
  ): UiPolicyConstraint[] {
    const policyType = formValue.policyType;
    switch (policyType) {
      case 'Time-Period-Restricted':
        return this.buildTimePeriodRestrictionPermissions(formValue);
      case 'Connector-Restricted-Usage':
        return [this.buildConnectorRestrictedConstraint(formValue)];
      default:
        throw new Error(`Unknown policyType: ${policyType}`);
    }
  }

  private buildConnectorRestrictedConstraint(
    formValue: NewPolicyDialogFormValue,
  ): UiPolicyConstraint {
    return {
      left: PolicyLeftExpressions.ReferringConnector,
      operator: 'EQ',
      right: {
        type: 'STRING',
        value: formValue.connectorId!,
      },
    };
  }

  private buildTimePeriodRestrictionPermissions(
    formValue: NewPolicyDialogFormValue,
  ): UiPolicyConstraint[] {
    const start = formValue.range!!.start!!;
    const constraints: UiPolicyConstraint[] = [
      this.evaluationTime('GEQ', start),
    ];

    const end = formValue.range!!.end;
    if (end) {
      constraints.push(this.evaluationTime('LT', addDays(end, 1)));
    }
    return constraints;
  }

  private evaluationTime(
    operator: OperatorDto,
    date: Date,
  ): UiPolicyConstraint {
    return {
      left: PolicyLeftExpressions.PolicyEvaluationTime,
      operator,
      right: {
        type: 'STRING',
        value: date.toISOString()!,
      },
    };
  }
}
