/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DateField} from '@/components/form/date-field';
import {
  readSingleStringLiteral,
  stringLiteral,
} from '@/components/policy-editor/core/policy-jsonld-utils';
import type {TreeNode} from '@/components/policy-editor/core/tree-node';
import {OperatorSelect} from '@/components/policy-editor/editor/operator-select';
import type {UsePolicyEditor} from '@/components/policy-editor/editor/use-policy-editor';
import type {PolicyEditorNodeValue} from '@/components/policy-editor/model/policy-editor-node-value';
import type {PolicyValueTypeAdapter} from '@/components/policy-editor/model/policy-value-type-adapter';
import {
  type PolicyEditorConstraintFormValue,
  type PolicyValueType,
} from '@/components/policy-editor/value-types/all';
import {
  localTzDayToIsoString,
  truncateToLocalTzDay,
  truncateToLocalTzDayRaw,
} from '@/lib/utils/date-utils';
import {dateRequiredButNullable} from '@/lib/utils/zod/schema-utils';
import {OperatorDto} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';
import {z} from 'zod';

export const datetimeTruncateToDateFormSchema = z.object({
  type: z.literal('DATETIME_TRUNCATE_TO_DATE' satisfies PolicyValueType),
  operator: z.nativeEnum(OperatorDto),
  date: dateRequiredButNullable(),
});

export type DatetimeTruncateToDateFormValue = z.infer<
  typeof datetimeTruncateToDateFormSchema
>;

export const datetimeTruncateToDateAdapter: PolicyValueTypeAdapter = {
  displayText: (literal, operator): string | null => {
    const stringOrNull = readSingleStringLiteral(literal);
    return safeConversion(stringOrNull, (string) => {
      const date = new Date(string);
      const upperBound = isUpperBound(operator);

      return truncateToLocalTzDay(date, upperBound);
    });
  },
  buildFormValueFn: (literal, operator): PolicyEditorConstraintFormValue => {
    const stringOrNull = readSingleStringLiteral(literal);
    const date = safeConversion(stringOrNull, (string) => {
      const date = new Date(string);
      const upperBound = isUpperBound(operator);

      // Editing datetimes from a different TZ as days has no good solution
      return truncateToLocalTzDayRaw(date, upperBound);
    });
    return {
      type: 'DATETIME_TRUNCATE_TO_DATE',
      operator: 'LT',
      date: date!,
    };
  },
  buildValueFn: (valuePoly) => {
    const value = valuePoly as DatetimeTruncateToDateFormValue;
    return stringLiteral(
      safeConversion(value.date, (it) => {
        const upperBound = isUpperBound(value.operator);
        return localTzDayToIsoString(it, upperBound);
      }),
    );
  },
  emptyConstraintValue: () => ({
    operator: 'LT',
    right: {
      type: 'STRING',
    },
  }),
};

export const PolicyEditorConstraintDate = ({
  policyEditor,
  treeNode,
}: {
  policyEditor: UsePolicyEditor;
  treeNode: TreeNode<PolicyEditorNodeValue>;
}) => {
  const t = useTranslations();
  return (
    <div className={'flex gap-4'}>
      <OperatorSelect policyEditor={policyEditor} treeNode={treeNode} />
      <DateField
        name={policyEditor.formKeyForNode(treeNode, 'date')}
        control={policyEditor.form.control}
        placeholder={treeNode.value.verb?.operandRightPlaceholder?.(t)}
        label={treeNode.value.verb?.operandRightTitle?.(t) ?? 'Unknown'}
        isRequired={true}
      />
    </div>
  );
};

const isUpperBound = (operator: OperatorDto) =>
  operator === 'GT' || operator === 'LEQ';

/**
 * Helper function for reducing mental complexity of mapping code:
 *  - Handles null input
 *  - Handles undefined output
 *  - Catches exceptions and returns null
 *
 * @param valueOrNull value
 * @param mapper mapper
 */
const safeConversion = <T, R>(
  valueOrNull: T | null | undefined,
  mapper: (it: T) => R | null | undefined,
): R | null => {
  if (valueOrNull == null) {
    return null;
  }

  try {
    return mapper(valueOrNull) ?? null;
  } catch {
    return null;
  }
};
