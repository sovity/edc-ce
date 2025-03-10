/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
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
