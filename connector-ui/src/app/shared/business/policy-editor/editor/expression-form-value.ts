/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {PolicyMultiExpressionConfig} from '../model/policy-multi-expressions';
import {PolicyOperatorConfig} from '../model/policy-operators';
import {PolicyVerbConfig} from '../model/policy-verbs';

export interface ExpressionFormValue {
  type: 'CONSTRAINT' | 'MULTI' | 'EMPTY';

  multiExpression?: PolicyMultiExpressionConfig;
  verb?: PolicyVerbConfig;
  supportedOperators?: PolicyOperatorConfig[];
}
