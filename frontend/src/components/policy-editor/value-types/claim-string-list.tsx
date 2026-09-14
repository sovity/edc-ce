/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import InputField from '@/components/form/input-field';
import {TagInputField} from '@/components/form/tag-input-field';
import type {TreeNode} from '@/components/policy-editor/core/tree-node';
import {OperatorSelect} from '@/components/policy-editor/editor/operator-select';
import type {UsePolicyEditor} from '@/components/policy-editor/editor/use-policy-editor';
import type {PolicyEditorNodeValue} from '@/components/policy-editor/model/policy-editor-node-value';
import {useTranslations} from 'next-intl';

export const PolicyEditorConstraintClaimStringList = ({
  policyEditor,
  treeNode,
}: {
  policyEditor: UsePolicyEditor;
  treeNode: TreeNode<PolicyEditorNodeValue>;
}) => {
  const t = useTranslations();
  return (
    <div className={'flex flex-col gap-4'}>
      <InputField
        name={policyEditor.formKeyForNode(treeNode, 'claimName')}
        control={policyEditor.form.control}
        label={t('General.Policies.Verbs.genericClaimNameTitle')}
        placeholder={t('General.Policies.Verbs.genericClaimNamePlaceholder')}
        description={t('General.Policies.Verbs.genericClaimNameDescription')}
        disableAutoComplete
        isRequired={true}
      />
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
    </div>
  );
};
