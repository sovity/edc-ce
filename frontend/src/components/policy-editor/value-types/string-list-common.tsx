/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import type {UsePolicyEditor} from '@/components/policy-editor/editor/use-policy-editor';
import type {TreeNode} from '@/components/policy-editor/core/tree-node';
import type {PolicyEditorNodeValue} from '@/components/policy-editor/model/policy-editor-node-value';
import {useTranslations} from 'next-intl';
import {OperatorSelect} from '@/components/policy-editor/editor/operator-select';
import {TagInputField} from '@/components/form/tag-input-field';

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
