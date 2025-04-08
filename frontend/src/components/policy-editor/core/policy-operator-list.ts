/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type PolicyOperatorConfig} from '@/components/policy-editor/model/policy-operator-config';
import {associateBy} from '@/lib/utils/map-utils';
import {byFallback} from '@/lib/utils/translation-utils';
import {type OperatorDto} from '@sovity.de/edc-client';

export class PolicyOperatorList {
  byId: Map<OperatorDto, PolicyOperatorConfig>;

  constructor(private operators: PolicyOperatorConfig[]) {
    this.byId = this.buildByIdMap();
  }

  getOperatorConfig(operator: OperatorDto): PolicyOperatorConfig {
    return (
      this.byId.get(operator) ?? this.defaultPolicyOperatorConfig(operator)
    );
  }

  getSupportedPolicyOperators(): PolicyOperatorConfig[] {
    return this.operators;
  }

  private buildByIdMap(): Map<OperatorDto, PolicyOperatorConfig> {
    return associateBy(this.getSupportedPolicyOperators(), (it) => it.id);
  }

  private defaultPolicyOperatorConfig(
    operator: OperatorDto,
  ): PolicyOperatorConfig {
    return {
      id: operator,
      title: byFallback(operator),
      description: byFallback(''),
    };
  }
}
