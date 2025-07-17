/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {readArrayLiteral} from '@/components/policy-editor/core/policy-jsonld-utils';
import type {PolicyValueTypeAdapter} from '@/components/policy-editor/model/policy-value-type-adapter';
import {
  type PolicyEditorConstraintFormValue,
  type PolicyValueType,
} from '@/components/policy-editor/value-types/all';
import {filterNonNull} from '@/lib/utils/array-utils';
import {OperatorDto} from '@sovity.de/edc-client';
import {z} from 'zod';

export const stringListWithCommaSupportFormSchema = z.object({
  type: z.literal('STRING_LIST_WITH_COMMA_SUPPORT' satisfies PolicyValueType),
  operator: z.nativeEnum(OperatorDto),
  stringList: z.array(z.string()).min(1),
});

export type StringListWithCommaSupportFormValue = z.infer<
  typeof stringListWithCommaSupportFormSchema
>;

export const stringListWithCommaSupportAdapter: PolicyValueTypeAdapter = {
  displayText: (literal): string | null => readArrayLiteral(literal).join(', '),
  buildFormValueFn: (literal): PolicyEditorConstraintFormValue => {
    let rawStrings: (string | null | undefined)[];
    if (literal.type === 'STRING') {
      rawStrings = [literal.value];
    } else {
      rawStrings = readArrayLiteral(literal);
    }
    return {
      type: 'STRING_LIST_WITH_COMMA_SUPPORT',
      operator: 'IN',
      stringList: filterNonNull(rawStrings)
        .flatMap((it) => it.split(','))
        .map((it) => it.trim())
        .filter((it) => it.length > 0),
    };
  },
  buildValueFn: (valuePoly) => {
    const value = valuePoly as StringListWithCommaSupportFormValue;
    const items = value.stringList ?? [];

    if (value.operator === 'EQ') {
      return {
        type: 'STRING',
        value: items.join(','),
      };
    }

    return {
      type: 'STRING_LIST',
      valueList: items,
    };
  },
  emptyConstraintValue: () => ({
    operator: 'IN',
    right: {
      type: 'STRING_LIST',
      valueList: [],
    },
  }),
};
