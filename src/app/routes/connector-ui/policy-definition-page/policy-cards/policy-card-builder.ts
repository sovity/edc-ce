import {Injectable} from '@angular/core';
import {
  PolicyDefinitionDto,
  PolicyDefinitionPage,
  UiPolicyLiteral,
} from '@sovity.de/edc-client';
import {OPERATOR_SYMBOLS} from '../../../../core/services/api/policy-type-ext';
import {PolicyCard, PolicyCardConstraint} from './policy-card';

@Injectable({providedIn: 'root'})
export class PolicyCardBuilder {
  buildPolicyCards(policyDefinitionPage: PolicyDefinitionPage): PolicyCard[] {
    return policyDefinitionPage.policies.map((policyDefinition) =>
      this.buildPolicyCard(policyDefinition),
    );
  }

  buildPolicyCard(policyDefinition: PolicyDefinitionDto): PolicyCard {
    const irregularities = policyDefinition.policy?.errors ?? [];
    return {
      id: policyDefinition.policyDefinitionId,
      isRegular: !irregularities.length,
      irregularities,
      constraints: this.buildPolicyCardConstraints(policyDefinition),
      objectForJson: JSON.parse(policyDefinition.policy.policyJsonLd),
    };
  }

  private buildPolicyCardConstraints(
    policyDefinition: PolicyDefinitionDto,
  ): PolicyCardConstraint[] {
    const constraints = policyDefinition.policy?.constraints ?? [];
    return constraints.map((constraint) => {
      let left = constraint.left;
      let operator = OPERATOR_SYMBOLS[constraint.operator];
      let right = this.policyLiteralToString(constraint.right);

      return {
        left,
        operator,
        right,
      };
    });
  }

  private policyLiteralToString(literal: UiPolicyLiteral): string {
    switch (literal.type) {
      case 'STRING':
        return literal.value ?? '';
      case 'STRING_LIST':
        return literal.valueList?.join(', ') ?? '';
      case 'JSON':
        return literal.value ?? '';
      default:
        throw new Error(`Unknown literal type: ${literal.type}`);
    }
  }
}
