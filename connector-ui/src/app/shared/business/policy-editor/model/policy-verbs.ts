/*
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 * Copyright 2025 sovity GmbH
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
 *     Fraunhofer FIT - contributed initial internationalization support
 *     sovity - continued development
 */
import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {OperatorDto} from '@sovity.de/edc-client';
import {LazyTranslation} from '../../../../core/utils/lazy-utils';
import {associateBy} from '../../../../core/utils/map-utils';
import {
  PolicyFormAdapter,
  jsonAdapter,
  localDateAdapter,
  stringArrayOrCommaJoinedAdapter,
} from './policy-form-adapter';
import {policyLeftExpressions} from './policy-left-expressions';
import {PolicyOperatorService} from './policy-operators';

export interface PolicyVerbConfig {
  operandLeftId: string;
  operandLeftTitle: string;
  operandLeftDescription: string;
  operandRightType: 'DATE' | 'TEXT' | 'PARTICIPANT_ID';
  operandRightHint?: string;
  operandRightPlaceholder?: string;
  supportedOperators: OperatorDto[];
  adapter: PolicyFormAdapter<any>;
}

@Injectable()
export class PolicyVerbService {
  byId: LazyTranslation<Map<string, PolicyVerbConfig>>;

  constructor(
    private translateService: TranslateService,
    private policyOperatorService: PolicyOperatorService,
  ) {
    this.byId = new LazyTranslation(this.translateService, () =>
      this.buildByIdMap(),
    );
  }

  getVerbConfig(verb: string): PolicyVerbConfig {
    return this.byId.getValue().get(verb) ?? this.getFallbackVerbConfig(verb);
  }

  getFallbackVerbConfig(verb: string): PolicyVerbConfig {
    return {
      operandLeftId: verb,
      operandLeftTitle: verb,
      operandLeftDescription: '',
      supportedOperators: this.policyOperatorService
        .getSupportedPolicyOperators()
        .map((it) => it.id),
      operandRightType: 'TEXT',
      adapter: jsonAdapter,
    };
  }

  getSupportedPolicyVerbs(): PolicyVerbConfig[] {
    return [
      {
        operandLeftId: policyLeftExpressions.referringConnector,
        operandLeftTitle: "Consumer's Participant ID",
        operandLeftDescription:
          "Consumer's Participant ID, also called Connector ID, of the counter-party connector.",
        operandRightType: 'PARTICIPANT_ID',
        supportedOperators: ['EQ', 'IN'],
        operandRightPlaceholder: 'MDSL1234XX.C1234YY',
        operandRightHint: 'Multiple values can be joined by comma',
        adapter: stringArrayOrCommaJoinedAdapter,
      },
      {
        operandLeftId: policyLeftExpressions.policyEvaluationTime,
        operandLeftTitle: 'Time Restriction',
        operandLeftDescription:
          'Time at which the policy is evaluated. This can be used to restrict the data offer to certain time periods',
        supportedOperators: ['GEQ', 'LEQ', 'GT', 'LT'],
        operandRightType: 'DATE',
        operandRightPlaceholder: 'DD/MM/YYYY',
        operandRightHint: 'DD/MM/YYYY',
        adapter: localDateAdapter,
      },
    ];
  }

  private buildByIdMap(): Map<string, PolicyVerbConfig> {
    return associateBy(
      this.getSupportedPolicyVerbs(),
      (it) => it.operandLeftId,
    );
  }
}
