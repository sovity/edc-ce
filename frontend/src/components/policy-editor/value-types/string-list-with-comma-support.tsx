/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {TagInputField} from '@/components/form/tag-input-field';
import {readArrayLiteral} from '@/components/policy-editor/core/policy-jsonld-utils';
import type {TreeNode} from '@/components/policy-editor/core/tree-node';
import {OperatorSelect} from '@/components/policy-editor/editor/operator-select';
import type {UsePolicyEditor} from '@/components/policy-editor/editor/use-policy-editor';
import type {PolicyEditorNodeValue} from '@/components/policy-editor/model/policy-editor-node-value';
import type {PolicyValueTypeAdapter} from '@/components/policy-editor/model/policy-value-type-adapter';
import {
  type PolicyEditorConstraintFormValue,
  type PolicyValueType,
} from '@/components/policy-editor/value-types/all';
import {filterNonNull} from '@/lib/utils/array-utils';
import {OperatorDto} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';
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

export const PolicyEditorConstraintStringList = ({
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
      <TagInputField
        className={'grow'}
        name={policyEditor.formKeyForNode(treeNode, 'stringList')}
        control={policyEditor.form.control}
        label={treeNode.value.verb?.operandRightTitle?.(t) ?? 'Unknown'}
        placeholder={treeNode.value.verb?.operandRightPlaceholder?.(t)}
        isRequired={true}
      />
    </div>
  );
};
