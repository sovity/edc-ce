import {Injectable} from '@angular/core';
import {
  OperatorDto,
  UiPolicyExpression,
  UiPolicyExpressionType,
  UiPolicyLiteral,
} from '@sovity.de/edc-client';
import {associateBy} from '../../../core/utils/map-utils';
import {PolicyExpressionMapped} from './policy-expression-mapped';
import {
  PolicyMultiExpressionConfig,
  SUPPORTED_MULTI_EXPRESSIONS,
} from './policy-multi-expressions';
import {
  PolicyOperatorConfig,
  SUPPORTED_POLICY_OPERATORS,
  defaultPolicyOperatorConfig,
} from './policy-operators';
import {
  PolicyVerbConfig,
  SUPPORTED_POLICY_VERBS,
  defaultPolicyVerbConfig,
} from './policy-verbs';

@Injectable({providedIn: 'root'})
export class PolicyMapper {
  verbs = associateBy(SUPPORTED_POLICY_VERBS, (it) => it.operandLeftId);
  operators = associateBy(SUPPORTED_POLICY_OPERATORS, (it) => it.id);
  multiExpressionTypes = associateBy(
    SUPPORTED_MULTI_EXPRESSIONS,
    (it) => it.expressionType,
  );

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
    const verb = this.getVerbConfig(expression.constraint?.left!);
    const operator = this.getOperatorConfig(expression.constraint?.operator!);
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
    const multiExpression = this.getMultiExpressionConfig(expression.type);
    const expressions = (expression.expressions ?? []).map((it) =>
      this.buildPolicy(it),
    );
    return {
      type: 'MULTI',
      multiExpression,
      expressions,
    };
  }

  private getVerbConfig(verb: string): PolicyVerbConfig {
    const verbConfig = this.verbs.get(verb);
    if (verbConfig) {
      return verbConfig;
    }

    return defaultPolicyVerbConfig(verb);
  }

  private getOperatorConfig(operator: OperatorDto): PolicyOperatorConfig {
    const operatorConfig = this.operators.get(operator);
    if (operatorConfig) {
      return operatorConfig;
    }

    return defaultPolicyOperatorConfig(operator);
  }

  private getMultiExpressionConfig(
    expressionType: UiPolicyExpressionType,
  ): PolicyMultiExpressionConfig {
    const multiExpressionType = this.multiExpressionTypes.get(expressionType);
    if (multiExpressionType) {
      return multiExpressionType;
    }

    return {
      expressionType,
      title: expressionType,
      description: '',
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
