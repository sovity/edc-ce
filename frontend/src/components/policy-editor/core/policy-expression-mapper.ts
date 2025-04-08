/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type PolicyVerbList} from '@/components/policy-editor/core/policy-verb-list';
import {type PolicyExpressionMapped} from '@/components/policy-editor/model/policy-expression-mapped';
import {type PolicyVerbConfig} from '@/components/policy-editor/model/policy-verb-config';
import {policyValueTypeAdaptersById} from '@/components/policy-editor/value-types/all';
import {
  type OperatorDto,
  type UiPolicyExpression,
  type UiPolicyLiteral,
} from '@sovity.de/edc-client';
import {type PolicyMultiExpressionList} from './policy-multi-expression-list';
import {type PolicyOperatorList} from './policy-operator-list';

export class PolicyExpressionMapper {
  constructor(
    private policyOperatorList: PolicyOperatorList,
    private policyMultiExpressionList: PolicyMultiExpressionList,
    private policyVerbList: PolicyVerbList,
  ) {}

  buildPolicyExpressionMapped(
    expression: UiPolicyExpression,
  ): PolicyExpressionMapped {
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
    const verb = this.policyVerbList.getVerbConfig(
      expression.constraint?.left ?? 'Broken Expression',
    );
    const operator = this.policyOperatorList.getOperatorConfig(
      expression.constraint?.operator ?? 'EQ',
    );
    const value = expression.constraint?.right;

    return {
      type: 'CONSTRAINT',
      verb,
      operator,
      valueRaw: value,
      valueJson: this.formatJson(value!),
      displayValue: this.formatValue(value, verb, operator.id) ?? 'null',
    };
  }

  private mapMultiExpression(
    expression: UiPolicyExpression,
  ): PolicyExpressionMapped {
    const multiExpression =
      this.policyMultiExpressionList.getMultiExpressionConfig(expression.type);
    const expressions = (expression.expressions ?? []).map((it) =>
      this.buildPolicyExpressionMapped(it),
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
    operator: OperatorDto,
  ) {
    if (value == null) {
      return '';
    }

    const adapter = policyValueTypeAdaptersById[verbConfig.valueType];
    return adapter.displayText(value, operator);
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
