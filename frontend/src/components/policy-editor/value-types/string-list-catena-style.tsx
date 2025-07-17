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

export const stringListCatenaStyleFormSchema = z.object({
  type: z.literal('STRING_LIST_CATENA_STYLE' satisfies PolicyValueType),
  operator: z.nativeEnum(OperatorDto),
  stringList: z.array(z.string()).min(1),
});

export type StringListCatenaStyleFormValue = z.infer<
  typeof stringListCatenaStyleFormSchema
>;

export const stringListCatenaStyleAdapter: PolicyValueTypeAdapter = {
  displayText: (literal): string | null => readArrayLiteral(literal).join(', '),
  buildFormValueFn: (literal, operator): PolicyEditorConstraintFormValue => {
    return {
      type: 'STRING_LIST_CATENA_STYLE',
      operator,
      stringList: filterNonNull(readArrayLiteral(literal)),
    };
  },
  buildValueFn: (valuePoly) => {
    const value = valuePoly as StringListCatenaStyleFormValue;
    const items = value.stringList ?? [];

    return {
      type: 'STRING_LIST',
      valueList: items,
    };
  },
  emptyConstraintValue: () => ({
    operator: 'IS_ANY_OF',
    right: {
      type: 'STRING_LIST',
      valueList: [],
    },
  }),
};
