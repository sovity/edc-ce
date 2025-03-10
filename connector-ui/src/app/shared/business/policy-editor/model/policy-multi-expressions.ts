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
import {UiPolicyExpressionType} from '@sovity.de/edc-client';
import {LazyTranslation} from '../../../../core/utils/lazy-utils';
import {associateBy} from '../../../../core/utils/map-utils';

export interface PolicyMultiExpressionConfig {
  expressionType: UiPolicyExpressionType;
  title: string;
  description: string;
}

@Injectable()
export class PolicyMultiExpressionService {
  byId: LazyTranslation<
    Map<UiPolicyExpressionType, PolicyMultiExpressionConfig>
  >;

  constructor(private translateService: TranslateService) {
    this.byId = new LazyTranslation(this.translateService, () =>
      this.buildByIdMap(),
    );
  }

  getMultiExpressionConfig(
    expressionType: UiPolicyExpressionType,
  ): PolicyMultiExpressionConfig {
    return (
      this.byId.getValue().get(expressionType) ??
      this.getFallbackMultiExpressionConfig(expressionType)
    );
  }

  getSupportedMultiExpressions(): PolicyMultiExpressionConfig[] {
    return [
      {
        expressionType: 'AND',
        title: 'AND',
        description:
          'Conjunction of several expressions. Evaluates to true if and only if all child expressions are true',
      },
      {
        expressionType: 'OR',
        title: 'OR',
        description:
          'Disjunction of several expressions. Evaluates to true if and only if at least one child expression is true',
      },
      {
        expressionType: 'XONE',
        title: 'XONE',
        description:
          'XONE operation. Evaluates to true if and only if exactly one child expression is true',
      },
    ];
  }

  private buildByIdMap(): Map<
    UiPolicyExpressionType,
    PolicyMultiExpressionConfig
  > {
    return associateBy(
      this.getSupportedMultiExpressions(),
      (it) => it.expressionType,
    );
  }

  private getFallbackMultiExpressionConfig(
    expressionType: UiPolicyExpressionType,
  ) {
    return {
      expressionType,
      title: expressionType,
      description: '',
    };
  }
}
