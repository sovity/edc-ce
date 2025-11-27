/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type PolicyContext} from '@/components/policy-editor/core/policy-context';
import {type PolicyExpressionMapped} from '@/components/policy-editor/model/policy-expression-mapped';
import type {PolicyDefinitionDto} from '@sovity.de/edc-client';

export interface PolicyDefinitionMapped {
  policyDefinitionId: string;
  errors: string[];
  policyExpression: PolicyExpressionMapped;
  policyJsonLd: string;
}

export const buildPolicyDefinitionMapped = (
  policyDefinition: PolicyDefinitionDto,
  policyContext: PolicyContext,
): PolicyDefinitionMapped => {
  const policyExpression =
    policyContext.policyExpressionMapper.buildPolicyExpressionMapped(
      policyDefinition.policy.expression!,
    );

  return {
    policyDefinitionId: policyDefinition.policyDefinitionId,
    policyExpression,
    policyJsonLd: policyDefinition.policy.policyJsonLd,
    errors: policyDefinition.policy.errors,
  };
};
