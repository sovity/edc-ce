/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import type {TreeNode} from '@/components/policy-editor/core/tree-node';
import type {UsePolicyEditor} from '@/components/policy-editor/editor/use-policy-editor';
import type {PolicyEditorNodeValue} from '@/components/policy-editor/model/policy-editor-node-value';
import {Button} from '@/components/ui/button';
import {Trash} from 'lucide-react';

export const PolicyEditorRemoveButton = ({
  policyEditor,
  treeNode,
}: {
  policyEditor: UsePolicyEditor;
  treeNode: TreeNode<PolicyEditorNodeValue>;
}) => {
  return (
    <Button
      dataTestId={`btn-policy-node-remove`}
      size="fit"
      variant="outline"
      className="flex justify-between gap-2 px-2 py-1.5"
      onClick={() => policyEditor.removeNode(treeNode.path)}>
      <Trash className="h-5 w-5" />
    </Button>
  );
};
