/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useState} from 'react';
import {type PolicyContext} from '@/components/policy-editor/core/policy-context';
import {
  findNode,
  type IdFactory,
  newTreeEmpty,
  newTreeFold,
  type TreeGeneratorFn,
  type TreeNode,
  withAppendedTree,
  withRemovedNode,
} from '@/components/policy-editor/core/tree-node';
import {
  type PolicyEditorFormValue,
  policyEditorNodeFormFields,
} from '@/components/policy-editor/editor/policy-editor-form-schema';
import {type PolicyEditorNodeValue} from '@/components/policy-editor/model/policy-editor-node-value';
import type {PolicyMultiExpressionConfig} from '@/components/policy-editor/model/policy-multi-expression-config';
import {type PolicyOperatorConfig} from '@/components/policy-editor/model/policy-operator-config';
import type {PolicyVerbConfig} from '@/components/policy-editor/model/policy-verb-config';
import {type PolicyEditorConstraintFormValue} from '@/components/policy-editor/value-types/all';
import {type UiPolicyExpression} from '@sovity.de/edc-client';
import {type UseFormReturn} from 'react-hook-form';

export const useIdFactory = (): IdFactory => {
  const [id, setId] = useState(0);
  return () => {
    setId((prev) => prev + 1);
    return id.toString();
  };
};

export const usePolicyEditor = (
  policyContext: PolicyContext,
  form: UseFormReturn<any>,
  formKey: string,
): UsePolicyEditor => {
  const idFactory = useIdFactory();
  const [tree, setTree] = useState<TreeNode<PolicyEditorNodeValue>>(
    newTreeEmpty<PolicyEditorNodeValue>({type: 'EMPTY'}),
  );

  const addConstraint = (parentPath: string[], verb: PolicyVerbConfig) => {
    addExpression(parentPath, {
      type: 'CONSTRAINT',
      constraint: {
        left: verb.operandLeftId,
        ...policyContext.getAdapter(verb).emptyConstraintValue(),
      },
    });
  };

  const addMultiExpression = (
    parentPath: string[],
    multi: PolicyMultiExpressionConfig,
  ) => {
    addExpression(parentPath, {
      type: multi.expressionType,
      expressions: [],
    });
  };

  const addExpression = (
    parentPath: string[],
    expression: UiPolicyExpression,
  ) => {
    setTree((rootNode) => {
      if (parentPath.length === 0 && rootNode.value.type === 'EMPTY') {
        return newTreeFold({
          value: expression,
          foldFn: treeGenerator,
          idFactory,
        });
      } else {
        return withAppendedTree({
          rootNode,
          parentPath,
          value: expression,
          foldFn: treeGenerator,
          idFactory,
        });
      }
    });
  };

  const removeNode = (path: string[]) => {
    deregisterConstraintFormControls(findNode(tree, path));
    setTree((rootNode) => {
      if (path.length === 0) {
        return newTreeEmpty({type: 'EMPTY'});
      } else {
        return withRemovedNode({root: rootNode, path});
      }
    });
  };

  const registerConstraintFormControls = (
    nodeId: string,
    formValue: PolicyEditorConstraintFormValue,
  ) => {
    allControlIdsOfNode(nodeId).forEach((control) => form.register(control));

    form.setValue(getControlKeyForNode(nodeId), formValue);
  };

  const deregisterConstraintFormControls = (
    node: TreeNode<PolicyEditorNodeValue>,
  ) => {
    const controlIds = allControlIdsOfTree(node);
    form.unregister(controlIds);
  };

  const getControlKeySegmentForNode = (nodeId: string) => `node_${nodeId}`;

  const getControlKeyForNode = (nodeId: string) =>
    `${formKey}.${getControlKeySegmentForNode(nodeId)}`;

  const getControlKeyForField = (nodeId: string, controlId: string) =>
    `${getControlKeyForNode(nodeId)}.${controlId}`;

  const getFormFieldName = (
    node: TreeNode<PolicyEditorNodeValue>,
    fieldName: string,
  ): string => getControlKeyForField(node.id, fieldName);

  const allControlIdsOfNode = (nodeId: string): string[] => {
    return policyEditorNodeFormFields.map((control) =>
      getControlKeyForField(nodeId, control),
    );
  };

  const allControlIdsOfTree = (
    node: TreeNode<PolicyEditorNodeValue>,
  ): string[] => {
    const controlIds: string[] = [];
    dfs(node, (it) => {
      if (it.value.type === 'CONSTRAINT') {
        controlIds.push(...allControlIdsOfNode(it.id));
      }
    });
    return controlIds;
  };

  const dfs = (
    node: TreeNode<PolicyEditorNodeValue>,
    callback: (node: TreeNode<PolicyEditorNodeValue>) => void,
  ) => {
    callback(node);
    Object.values(node.children).forEach((child) => dfs(child, callback));
  };

  /**
   * Fold UiPolicyExpression -> TreeNode<PolicyExpressionNodeValue> + React Hook Forms
   */
  const treeGenerator: TreeGeneratorFn<
    UiPolicyExpression,
    PolicyEditorNodeValue
  > = (original, nodeId) => {
    let type: 'EMPTY' | 'CONSTRAINT' | 'MULTI';
    let multiExpression: PolicyMultiExpressionConfig | undefined = undefined;
    let verb: PolicyVerbConfig | undefined = undefined;
    let supportedOperators: PolicyOperatorConfig[] = [];

    switch (original.type) {
      case 'EMPTY':
        type = 'EMPTY';
        break;
      case 'CONSTRAINT':
        type = 'CONSTRAINT';
        const constraint = original.constraint!;
        verb = policyContext.policyVerbList.getVerbConfig(constraint.left);
        supportedOperators = policyContext.getSupportedOperators(verb);

        // Register Form Controls
        const constraintFormValue = policyContext
          .getAdapter(verb)
          .buildFormValueFn(constraint.right, constraint.operator);
        registerConstraintFormControls(nodeId, constraintFormValue);
        break;
      default:
        type = 'MULTI';
        multiExpression =
          policyContext.policyMultiExpressionList.getMultiExpressionConfig(
            original.type,
          );
        break;
    }

    return {
      value: {
        type,
        multiExpression,
        verb,
        supportedOperators,
      } satisfies PolicyEditorNodeValue,
      children: original.expressions ?? [],
    };
  };

  /**
   * Fold TreeNode<PolicyExpressionNodeValue> + React Hook Forms -> UiPolicyExpression
   */
  const getUiPolicyExpression = (): UiPolicyExpression => {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    const formValueAllNodes: PolicyEditorFormValue = form.getValues(formKey);

    const fold = (
      node: TreeNode<PolicyEditorNodeValue>,
    ): UiPolicyExpression => {
      const value = node.value;
      if (value.type === 'EMPTY') {
        return {type: 'EMPTY'};
      } else if (value.type === 'MULTI') {
        return {
          type: value.multiExpression!.expressionType,
          expressions: Object.values(node.children).map((child) => fold(child)),
        };
      } else {
        const formValueNode =
          formValueAllNodes[getControlKeySegmentForNode(node.id)]!;
        const adapter = policyContext.getAdapter(value.verb!);
        return {
          type: 'CONSTRAINT',
          constraint: {
            left: value.verb!.operandLeftId,
            operator: formValueNode.operator,
            right: adapter.buildValueFn(formValueNode),
          },
        };
      }
    };

    return fold(tree);
  };

  return {
    policyContext,
    tree,
    addConstraint,
    addMultiExpression,
    addExpression,
    removeNode,
    form,
    formKey,
    formKeyForNode: getFormFieldName,
    getUiPolicyExpression,
  };
};

export interface UsePolicyEditor {
  policyContext: PolicyContext;
  tree: TreeNode<PolicyEditorNodeValue>;
  addConstraint: (parentPath: string[], verb: PolicyVerbConfig) => void;
  addMultiExpression: (
    parentPath: string[],
    multi: PolicyMultiExpressionConfig,
  ) => void;
  addExpression: (parentPath: string[], expression: UiPolicyExpression) => void;
  removeNode: (path: string[]) => void;
  form: UseFormReturn<any>;
  formKey: string;
  formKeyForNode: (
    node: TreeNode<PolicyEditorNodeValue>,
    fieldName: string,
  ) => string;
  getUiPolicyExpression: () => UiPolicyExpression;
}
