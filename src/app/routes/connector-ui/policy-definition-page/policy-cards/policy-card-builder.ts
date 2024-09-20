import {Injectable} from '@angular/core';
import {PolicyDefinitionDto, PolicyDefinitionPage} from '@sovity.de/edc-client';
import {PolicyMapper} from '../../../../shared/business/policy-editor/model/policy-mapper';
import {PolicyCard} from './policy-card';

@Injectable({providedIn: 'root'})
export class PolicyCardBuilder {
  constructor(private policyMapper: PolicyMapper) {}
  buildPolicyCards(policyDefinitionPage: PolicyDefinitionPage): PolicyCard[] {
    return policyDefinitionPage.policies.map((policyDefinition) =>
      this.buildPolicyCard(policyDefinition),
    );
  }

  buildPolicyCard(policyDefinition: PolicyDefinitionDto): PolicyCard {
    const irregularities = policyDefinition.policy?.errors ?? [];
    const expression = this.policyMapper.buildPolicy(
      policyDefinition.policy.expression!,
    );
    return {
      id: policyDefinition.policyDefinitionId,
      isRegular: !irregularities.length,
      irregularities,
      expression,
      searchText: JSON.stringify(expression),
      objectForJson: JSON.parse(policyDefinition.policy.policyJsonLd),
    };
  }
}
