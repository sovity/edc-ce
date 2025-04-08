/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {readJsonLiteral} from '@/components/policy-editor/core/policy-jsonld-utils';
import type {PolicyValueTypeAdapter} from '@/components/policy-editor/model/policy-value-type-adapter';
import {
  type PolicyEditorConstraintFormValue,
  type PolicyValueType,
} from '@/components/policy-editor/value-types/all';
import {jsonString} from '@/lib/utils/zod/schema-utils';
import {OperatorDto} from '@sovity.de/edc-client';
import {z} from 'zod';

export const rawJsonFormSchema = z.object({
  type: z.literal('RAW_JSON' satisfies PolicyValueType),
  operator: z.nativeEnum(OperatorDto),
  rawJson: jsonString(),
});

export type RawJsonFormValue = z.infer<typeof rawJsonFormSchema>;

export const rawJsonAdapter: PolicyValueTypeAdapter = {
  displayText: (literal) => readJsonLiteral(literal),
  buildFormValueFn: (literal, operator): PolicyEditorConstraintFormValue => ({
    type: 'RAW_JSON',
    operator,
    rawJson: readJsonLiteral(literal),
  }),
  buildValueFn: (valuePoly) => {
    const value = valuePoly as RawJsonFormValue;
    return {
      type: 'JSON',
      value: value.rawJson,
    };
  },
  emptyConstraintValue: () => ({
    operator: 'EQ',
    right: {
      type: 'JSON',
      value: 'null',
    },
  }),
};
