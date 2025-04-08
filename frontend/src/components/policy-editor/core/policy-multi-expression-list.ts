/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type PolicyMultiExpressionConfig} from '@/components/policy-editor/model/policy-multi-expression-config';
import {associateBy} from '@/lib/utils/map-utils';
import {byFallback} from '@/lib/utils/translation-utils';
import {type UiPolicyExpressionType} from '@sovity.de/edc-client';

export class PolicyMultiExpressionList {
  byId: Map<UiPolicyExpressionType, PolicyMultiExpressionConfig>;

  constructor(private multiExpressions: PolicyMultiExpressionConfig[]) {
    this.byId = this.buildByIdMap();
  }

  getMultiExpressionConfig(
    expressionType: UiPolicyExpressionType,
  ): PolicyMultiExpressionConfig {
    return (
      this.byId.get(expressionType) ??
      this.getFallbackMultiExpressionConfig(expressionType)
    );
  }

  getSupportedMultiExpressions(): PolicyMultiExpressionConfig[] {
    return this.multiExpressions;
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
  ): PolicyMultiExpressionConfig {
    return {
      expressionType,
      title: byFallback(expressionType),
      description: byFallback(''),
    };
  }
}
