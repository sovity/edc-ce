/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type PolicyMultiExpressionConfig} from '@/components/policy-editor/model/policy-multi-expression-config';
import {type PolicyOperatorConfig} from '@/components/policy-editor/model/policy-operator-config';
import {type PolicyVerbConfig} from '@/components/policy-editor/model/policy-verb-config';
import {type UiPolicyLiteral} from '@sovity.de/edc-client';

export interface PolicyExpressionMapped {
  type: 'CONSTRAINT' | 'MULTI' | 'EMPTY';

  multiExpression?: PolicyMultiExpressionConfig;
  expressions?: PolicyExpressionMapped[];

  verb?: PolicyVerbConfig;
  operator?: PolicyOperatorConfig;
  valueRaw?: UiPolicyLiteral;
  valueJson?: string;
  displayValue?: string;
}
