/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import SelectField from '@/components/form/select-field';
import {type TreeNode} from '@/components/policy-editor/core/tree-node';
import {type UsePolicyEditor} from '@/components/policy-editor/editor/use-policy-editor';
import {type PolicyEditorNodeValue} from '@/components/policy-editor/model/policy-editor-node-value';
import {type UiSelectItemOption} from '@/model/ui-select-item-option';
import {useTranslations} from 'next-intl';

export const OperatorSelect = ({
  policyEditor,
  treeNode,
}: {
  policyEditor: UsePolicyEditor;
  treeNode: TreeNode<PolicyEditorNodeValue>;
}) => {
  const t = useTranslations();
  return (
    <SelectField
      control={policyEditor.form.control}
      name={policyEditor.formKeyForNode(treeNode, 'operator')}
      label={t('General.policyOperator')}
      placeholder={t('General.policyOperator')}
      isRequired
      className={'min-w-[10rem]'}
      items={
        treeNode.value.supportedOperators?.map(
          (it): UiSelectItemOption => ({
            id: it.id,
            label: it.description(t),
          }),
        ) ?? []
      }
    />
  );
};
