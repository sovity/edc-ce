/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import type {PolicyVerbConfig} from '@/components/policy-editor/model/policy-verb-config';
import {byTranslation} from '@/lib/utils/translation-utils';

export function getSovityPolicyVerbs(): PolicyVerbConfig[] {
  return [
    {
      operandLeftIds: ['REFERRING_CONNECTOR'],
      operandLeftTitle: byTranslation(
        'General.Policies.Verbs.referringConnectorOperandLeftTitle',
      ),
      operandLeftDescription: byTranslation(
        'General.Policies.Verbs.referringConnectorOperandLeftDescription',
      ),

      operandRightTitle: byTranslation(
        'General.Policies.Verbs.referringConnectorOperandRightTitle',
      ),
      operandRightPlaceholder: byTranslation(
        'General.Policies.Verbs.referringConnectorOperandRightPlaceholder',
      ),

      supportedOperators: ['EQ', 'IN'],
      valueType: 'STRING_LIST_WITH_COMMA_SUPPORT',
    },
    {
      operandLeftIds: ['POLICY_EVALUATION_TIME'],
      operandLeftTitle: byTranslation(
        'General.Policies.Verbs.evaluationTimeOperandLeftTitle',
      ),
      operandLeftDescription: byTranslation(
        'General.Policies.Verbs.evaluationTimeOperandLeftDescription',
      ),

      operandRightTitle: byTranslation(
        'General.Policies.Verbs.evaluationTimeOperandRightTitle',
      ),
      operandRightPlaceholder: byTranslation(
        'General.Policies.Verbs.evaluationTimeOperandRightPlaceholder',
      ),

      supportedOperators: ['GEQ', 'LEQ', 'GT', 'LT'],
      valueType: 'DATETIME_TRUNCATE_TO_DATE',
    },
  ];
}
