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
