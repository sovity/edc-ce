/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type PolicyOperatorConfig} from '@/components/policy-editor/model/policy-operator-config';
import {type PolicyVerbConfig} from '@/components/policy-editor/model/policy-verb-config';
import {associateBy} from '@/lib/utils/map-utils';
import {byFallback} from '@/lib/utils/translation-utils';

export class PolicyVerbList {
  private byId: Map<string, PolicyVerbConfig>;

  constructor(
    private verbs: PolicyVerbConfig[],
    private operators: PolicyOperatorConfig[],
  ) {
    this.byId = this.buildByIdMap();
  }

  getVerbConfig(verb: string): PolicyVerbConfig {
    return this.byId.get(verb) ?? this.getFallbackVerbConfig(verb);
  }

  private getFallbackVerbConfig(verb: string): PolicyVerbConfig {
    return {
      operandLeftId: verb,
      operandLeftTitle: byFallback(verb),
      operandLeftDescription: byFallback(''),
      supportedOperators: this.operators.map((it) => it.id),
      valueType: 'RAW_JSON',
    };
  }

  getSupportedPolicyVerbs(): PolicyVerbConfig[] {
    return this.verbs;
  }

  private buildByIdMap(): Map<string, PolicyVerbConfig> {
    return associateBy(
      this.getSupportedPolicyVerbs(),
      (it) => it.operandLeftId,
    );
  }
}
