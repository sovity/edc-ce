/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type TreeNode} from '@/components/policy-editor/core/tree-node';
import PolicyEditorAddButton from '@/components/policy-editor/editor/policy-editor-add-button';
import {PolicyEditorRemoveButton} from '@/components/policy-editor/editor/policy-editor-remove-button';
import {type UsePolicyEditor} from '@/components/policy-editor/editor/use-policy-editor';
import {type PolicyEditorNodeValue} from '@/components/policy-editor/model/policy-editor-node-value';
import {PolicyConstraintVerbLabel} from '@/components/policy-editor/renderer/policy-constraint-verb-label';
import {PolicyMultiExpressionLabel} from '@/components/policy-editor/renderer/policy-multi-expression-label';
import {TreeLike} from '@/components/policy-editor/renderer/tree-like';
import {PolicyEditorConstraintForm} from '@/components/policy-editor/value-types/all';
import {useTranslations} from 'next-intl';

const PolicyEditor = ({policyEditor}: {policyEditor: UsePolicyEditor}) => {
  return (
    <PolicyEditorExpression
      treeNode={policyEditor.tree}
      policyEditor={policyEditor}
    />
  );
};

const PolicyEditorExpression = ({
  policyEditor,
  treeNode,
}: {
  policyEditor: UsePolicyEditor;
  treeNode: TreeNode<PolicyEditorNodeValue>;
}) => {
  return (
    <div className="flex grow flex-col justify-stretch">
      {treeNode.value.type === 'EMPTY' && (
        <PolicyEditorExpressionEmpty
          treeNode={treeNode}
          policyEditor={policyEditor}
        />
      )}
      {treeNode.value.type === 'CONSTRAINT' && (
        <PolicyEditorExpressionConstraint
          treeNode={treeNode}
          policyEditor={policyEditor}
        />
      )}
      {treeNode.value.type === 'MULTI' && (
        <PolicyEditorExpressionMulti
          treeNode={treeNode}
          policyEditor={policyEditor}
        />
      )}
    </div>
  );
};

const PolicyEditorExpressionEmpty = ({
  policyEditor,
  treeNode,
}: {
  policyEditor: UsePolicyEditor;
  treeNode: TreeNode<PolicyEditorNodeValue>;
}) => {
  return (
    <div>
      <PolicyEditorAddButton policyEditor={policyEditor} treeNode={treeNode} />
    </div>
  );
};

const PolicyEditorExpressionConstraint = ({
  policyEditor,
  treeNode,
}: {
  policyEditor: UsePolicyEditor;
  treeNode: TreeNode<PolicyEditorNodeValue>;
}) => {
  const t = useTranslations();
  return (
    <div className={'flex flex-col gap-4'}>
      <div className={'flex gap-1'}>
        <span className={'flex grow flex-col'}>
          <PolicyConstraintVerbLabel verb={treeNode.value.verb!} />
          <span className={'text-sm'}>
            {treeNode.value.verb!.operandLeftDescription(t)}
          </span>
        </span>
        <div>
          <PolicyEditorRemoveButton
            policyEditor={policyEditor}
            treeNode={treeNode}
          />
        </div>
      </div>
      <PolicyEditorConstraintForm
        policyEditor={policyEditor}
        treeNode={treeNode}
      />
    </div>
  );
};

const PolicyEditorExpressionMulti = ({
  policyEditor,
  treeNode,
}: {
  policyEditor: UsePolicyEditor;
  treeNode: TreeNode<PolicyEditorNodeValue>;
}) => {
  return (
    <TreeLike
      childContainerClassName={'gap-4'}
      header={
        <div className={'flex gap-1'}>
          <div className={'grow'}>
            <PolicyMultiExpressionLabel
              expression={treeNode.value.multiExpression!}
            />
          </div>
          <div>
            <PolicyEditorRemoveButton
              policyEditor={policyEditor}
              treeNode={treeNode}
            />
          </div>
        </div>
      }>
      {Object.values(treeNode.children).map((childNode) => (
        <PolicyEditorExpression
          key={childNode.id}
          treeNode={childNode}
          policyEditor={policyEditor}
        />
      ))}
      <div>
        <PolicyEditorAddButton
          policyEditor={policyEditor}
          treeNode={treeNode}
        />
      </div>
    </TreeLike>
  );
};

export default PolicyEditor;
