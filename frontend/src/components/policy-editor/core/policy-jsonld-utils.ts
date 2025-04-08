/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {filterNonNull} from '@/lib/utils/array-utils';
import {type UiPolicyLiteral} from '@sovity.de/edc-client';

export const readSingleStringLiteral = (
  literal: UiPolicyLiteral,
): string | null => {
  if (literal.type === 'STRING') {
    return literal.value ?? null;
  } else if (literal.type === 'STRING_LIST') {
    return literal.valueList?.length && literal.valueList[0] != null
      ? literal.valueList[0]
      : null;
  }
  return null;
};

export const readArrayLiteral = (literal: UiPolicyLiteral): string[] => {
  if (literal.type === 'STRING') {
    return filterNonNull([literal.value]);
  } else if (literal.type === 'STRING_LIST') {
    return literal.valueList ?? [];
  }
  return [];
};

export const readJsonLiteral = (literal: UiPolicyLiteral): string => {
  if (literal.type === 'STRING') {
    return JSON.stringify(literal.value);
  } else if (literal.type === 'STRING_LIST') {
    return JSON.stringify(literal.valueList);
  }
  return literal.value ?? 'null';
};

export const stringLiteral = (
  value: string | null | undefined,
): UiPolicyLiteral => ({
  type: 'STRING',
  value: value ?? undefined,
});
