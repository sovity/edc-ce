/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {PolicyExpressionMapper} from '@/components/policy-editor/core/policy-expression-mapper';
import {PolicyMultiExpressionList} from '@/components/policy-editor/core/policy-multi-expression-list';
import {PolicyOperatorList} from '@/components/policy-editor/core/policy-operator-list';
import {PolicyVerbList} from '@/components/policy-editor/core/policy-verb-list';
import {type PolicyMultiExpressionConfig} from '@/components/policy-editor/model/policy-multi-expression-config';
import {type PolicyOperatorConfig} from '@/components/policy-editor/model/policy-operator-config';
import {type PolicyValueTypeAdapter} from '@/components/policy-editor/model/policy-value-type-adapter';
import {type PolicyVerbConfig} from '@/components/policy-editor/model/policy-verb-config';
import {policyValueTypeAdaptersById} from '@/components/policy-editor/value-types/all';

/**
 * All classes for working with policies,
 * given a configuration of supported policies,
 * given a specific dataspace
 */
export class PolicyContext {
  policyMultiExpressionList: PolicyMultiExpressionList;
  policyOperatorList: PolicyOperatorList;
  policyVerbList: PolicyVerbList;
  policyExpressionMapper: PolicyExpressionMapper;

  constructor(
    supportedMultiExpressions: PolicyMultiExpressionConfig[],
    supportedOperators: PolicyOperatorConfig[],
    supportedVerbs: PolicyVerbConfig[],
  ) {
    this.policyMultiExpressionList = new PolicyMultiExpressionList(
      supportedMultiExpressions,
    );
    this.policyOperatorList = new PolicyOperatorList(supportedOperators);
    this.policyVerbList = new PolicyVerbList(
      supportedVerbs,
      supportedOperators,
    );
    this.policyExpressionMapper = new PolicyExpressionMapper(
      this.policyOperatorList,
      this.policyMultiExpressionList,
      this.policyVerbList,
    );
  }

  getAdapter(verb: PolicyVerbConfig): PolicyValueTypeAdapter {
    return policyValueTypeAdaptersById[verb.valueType];
  }

  getSupportedOperators(verb: PolicyVerbConfig): PolicyOperatorConfig[] {
    return this.policyOperatorList
      .getSupportedPolicyOperators()
      .filter((it) => verb.supportedOperators.includes(it.id));
  }
}
