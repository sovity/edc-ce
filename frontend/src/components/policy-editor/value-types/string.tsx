/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  readArrayLiteral,
  readSingleStringLiteral,
} from '@/components/policy-editor/core/policy-jsonld-utils';
import type {PolicyValueTypeAdapter} from '@/components/policy-editor/model/policy-value-type-adapter';
import {
  type PolicyEditorConstraintFormValue,
  type PolicyValueType,
} from '@/components/policy-editor/value-types/all';
import {OperatorDto} from '@sovity.de/edc-client';
import {z} from 'zod';
import type {UsePolicyEditor} from '@/components/policy-editor/editor/use-policy-editor';
import type {TreeNode} from '@/components/policy-editor/core/tree-node';
import type {PolicyEditorNodeValue} from '@/components/policy-editor/model/policy-editor-node-value';
import {useTranslations} from 'next-intl';
import {OperatorSelect} from '@/components/policy-editor/editor/operator-select';
import InputField from '@/components/form/input-field';

export const stringFormSchema = z.object({
  type: z.literal('STRING' satisfies PolicyValueType),
  operator: z.nativeEnum(OperatorDto),
  string: z.string().min(1),
});

export type StringFormValue = z.infer<typeof stringFormSchema>;

export const stringAdapter: PolicyValueTypeAdapter = {
  displayText: (literal): string | null => readArrayLiteral(literal).join(', '),
  buildFormValueFn: (literal, operator): PolicyEditorConstraintFormValue => {
    return {
      type: 'STRING',
      operator,
      string: readSingleStringLiteral(literal) ?? '',
    };
  },
  buildValueFn: (valuePoly) => {
    const value = valuePoly as StringFormValue;
    return {
      type: 'STRING',
      value: value.string,
    };
  },
  emptyConstraintValue: () => ({
    operator: 'EQ',
    right: {
      type: 'STRING',
      value: '',
    },
  }),
};
export const PolicyEditorConstraintString = ({
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
      <InputField
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
