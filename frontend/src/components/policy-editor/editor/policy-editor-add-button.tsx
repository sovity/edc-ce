/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type TreeNode} from '@/components/policy-editor/core/tree-node';
import {type UsePolicyEditor} from '@/components/policy-editor/editor/use-policy-editor';
import {type PolicyEditorNodeValue} from '@/components/policy-editor/model/policy-editor-node-value';
import {Button} from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {Plus} from 'lucide-react';
import {useTranslations} from 'next-intl';

const PolicyEditorAddButton = ({
  policyEditor,
  treeNode,
}: {
  policyEditor: UsePolicyEditor;
  treeNode: TreeNode<PolicyEditorNodeValue>;
}) => {
  const t = useTranslations();

  const availableVerbs =
    policyEditor.policyContext.policyVerbList.getSupportedPolicyVerbs();

  const availableMultiExpressions =
    policyEditor.policyContext.policyMultiExpressionList.getSupportedMultiExpressions();

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          dataTestId={`btn-policy-node-add`}
          size="fit"
          variant="outline"
          className="flex justify-between gap-2 px-2 py-1.5">
          <Plus className="h-5 w-5" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent>
        {availableMultiExpressions.map((multi) => (
          <DropdownMenuItem
            key={multi.expressionType}
            onClick={() =>
              policyEditor.addMultiExpression(treeNode.path, multi)
            }>
            {multi.title(t)}
          </DropdownMenuItem>
        ))}
        <DropdownMenuSeparator />
        {availableVerbs.map((verb) => (
          <DropdownMenuItem
            key={verb.operandLeftId}
            onClick={() => policyEditor.addConstraint(treeNode.path, verb)}>
            {verb.operandLeftTitle(t)}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default PolicyEditorAddButton;
