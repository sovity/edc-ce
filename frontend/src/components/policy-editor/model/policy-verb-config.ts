/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type PolicyValueType} from '@/components/policy-editor/value-types/all';
import {type TranslatedString} from '@/lib/utils/translation-utils';
import {type OperatorDto} from '@sovity.de/edc-client';

export interface PolicyVerbConfig {
  operandLeftIds: [string, ...string[]];

  /**
   * Set for verbs whose left operand is dynamic, e.g. "POLICY_CLAIM_" + user-provided claim name.
   * Constraints with a left operand starting with this prefix are mapped to this verb.
   */
  operandLeftPrefix?: string;

  operandLeftTitle: TranslatedString;
  operandLeftDescription: TranslatedString;
  operandRightTitle?: TranslatedString;
  operandRightPlaceholder?: TranslatedString;
  supportedOperators: OperatorDto[];
  valueType: PolicyValueType;
}
