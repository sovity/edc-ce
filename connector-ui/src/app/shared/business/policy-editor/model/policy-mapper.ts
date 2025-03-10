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
import {UiPolicyExpression, UiPolicyLiteral} from '@sovity.de/edc-client';
import {PolicyExpressionMapped} from './policy-expression-mapped';
import {PolicyMultiExpressionService} from './policy-multi-expressions';
import {PolicyOperatorConfig, PolicyOperatorService} from './policy-operators';
import {PolicyVerbConfig, PolicyVerbService} from './policy-verbs';

@Injectable()
export class PolicyMapper {
  constructor(
    private policyOperatorService: PolicyOperatorService,
    private policyMultiExpressionService: PolicyMultiExpressionService,
    private policyVerbService: PolicyVerbService,
  ) {}

  buildPolicy(expression: UiPolicyExpression): PolicyExpressionMapped {
    if (expression.type === 'EMPTY') {
      return {type: 'EMPTY'};
    }

    if (expression.type === 'CONSTRAINT') {
      return this.mapConstraint(expression);
    }

    return this.mapMultiExpression(expression);
  }

  private mapConstraint(
    expression: UiPolicyExpression,
  ): PolicyExpressionMapped {
    const verb = this.policyVerbService.getVerbConfig(
      expression.constraint?.left!,
    );
    const operator = this.policyOperatorService.getOperatorConfig(
      expression.constraint?.operator!,
    );
    const value = expression.constraint?.right;

    return {
      type: 'CONSTRAINT',
      verb,
      operator,
      valueRaw: value,
      valueJson: this.formatJson(value!),
      displayValue: this.formatValue(value, verb, operator) ?? 'null',
    };
  }

  private mapMultiExpression(
    expression: UiPolicyExpression,
  ): PolicyExpressionMapped {
    const multiExpression =
      this.policyMultiExpressionService.getMultiExpressionConfig(
        expression.type,
      );
    const expressions = (expression.expressions ?? []).map((it) =>
      this.buildPolicy(it),
    );
    return {
      type: 'MULTI',
      multiExpression,
      expressions,
    };
  }

  private formatValue(
    value: UiPolicyLiteral | undefined,
    verbConfig: PolicyVerbConfig,
    operatorConfig: PolicyOperatorConfig,
  ) {
    if (value == null) {
      return '';
    }

    return verbConfig.adapter.displayText(value, operatorConfig);
  }

  private formatJson(value: UiPolicyLiteral): string {
    if (value.type === 'STRING_LIST') {
      return JSON.stringify(value.valueList);
    }

    if (value.type === 'JSON') {
      return value.value ?? '';
    }

    return JSON.stringify(value.value);
  }
}
