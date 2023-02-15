import {Injectable} from '@angular/core';
import {PolicyDefinition, policyDefinitionId} from '../../../edc-dmgmt-client';
import {PolicyDefinitionUtils} from '../../services/policy-definition-utils';
import {
  AtomicConstraint,
  LiteralExpression,
  OperatorSymbols,
} from '../../services/policy-type-ext';
import {PolicyCard, PolicyCardConstraint} from './policy-card';

@Injectable({providedIn: 'root'})
export class PolicyCardBuilder {
  constructor(private policyDefinitionUtils: PolicyDefinitionUtils) {}

  buildPolicyCards(policyDefinitions: PolicyDefinition[]): PolicyCard[] {
    return policyDefinitions.map((policyDefinition) =>
      this.buildPolicyCard(policyDefinition),
    );
  }

  buildPolicyCard(policyDefinition: PolicyDefinition): PolicyCard {
    const irregularities =
      this.policyDefinitionUtils.getPolicyIrregularities(policyDefinition);
    return {
      id: policyDefinitionId(policyDefinition),
      isRegular: !irregularities.length,
      irregularities,
      constraints: this.buildPolicyCardConstraints(policyDefinition),
      objectForJson: policyDefinition,
    };
  }

  private buildPolicyCardConstraints(
    policyDefinition: PolicyDefinition,
  ): PolicyCardConstraint[] {
    const constraints: AtomicConstraint[] =
      policyDefinition.policy.permissions
        ?.map((it) => (it?.constraints ?? []) as AtomicConstraint[])
        ?.flat()
        ?.filter((constraint) =>
          this.policyDefinitionUtils.isAtomicConstraint(constraint),
        ) ?? [];
    return constraints.map((constraint) => {
      let rightStringValue = (constraint.rightExpression as LiteralExpression)
        .value;
      let leftStringValue = (constraint.leftExpression as LiteralExpression)
        .value;

      return {
        left: leftStringValue,
        operator: OperatorSymbols[constraint.operator] ?? constraint.operator,
        right: rightStringValue,
      };
    });
  }
}
