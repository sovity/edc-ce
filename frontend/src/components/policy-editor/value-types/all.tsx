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
import type {PolicyValueTypeAdapter} from '@/components/policy-editor/model/policy-value-type-adapter';
import {
  datetimeTruncateToDateAdapter,
  datetimeTruncateToDateFormSchema,
  PolicyEditorConstraintDate,
} from '@/components/policy-editor/value-types/datetime-truncate-to-date';
import {
  rawJsonAdapter,
  rawJsonFormSchema,
} from '@/components/policy-editor/value-types/raw-json-form-schema';
import {
  PolicyEditorConstraintStringList,
  stringListWithCommaSupportAdapter,
  stringListWithCommaSupportFormSchema,
} from '@/components/policy-editor/value-types/string-list-with-comma-support';
import {z} from 'zod';

export type PolicyValueType =
  | 'RAW_JSON'
  | 'DATETIME_TRUNCATE_TO_DATE'
  | 'STRING_LIST_WITH_COMMA_SUPPORT';

/**
 * All known policy value types
 */
export const policyValueTypeAdaptersById: Record<
  PolicyValueType,
  PolicyValueTypeAdapter
> = {
  RAW_JSON: rawJsonAdapter,
  DATETIME_TRUNCATE_TO_DATE: datetimeTruncateToDateAdapter,
  STRING_LIST_WITH_COMMA_SUPPORT: stringListWithCommaSupportAdapter,
};

/**
 * Discriminated Union for a Policy Editor Constraint Node
 */
export const policyEditorConstraintFormSchema = z.discriminatedUnion('type', [
  rawJsonFormSchema,
  datetimeTruncateToDateFormSchema,
  stringListWithCommaSupportFormSchema,
]);

/**
 * Discriminated Union for a Policy Editor Constraint Node
 */
export type PolicyEditorConstraintFormValue = z.infer<
  typeof policyEditorConstraintFormSchema
>;

/**
 * Policy Editor - Constraint Form
 */
export const PolicyEditorConstraintForm = ({
  policyEditor,
  treeNode,
}: {
  policyEditor: UsePolicyEditor;
  treeNode: TreeNode<PolicyEditorNodeValue>;
}) => {
  // There is no way to create Raw Json type constraints right now
  return (
    <>
      {treeNode.value.verb?.valueType === 'DATETIME_TRUNCATE_TO_DATE' && (
        <PolicyEditorConstraintDate
          policyEditor={policyEditor}
          treeNode={treeNode}
        />
      )}
      {treeNode.value.verb?.valueType === 'STRING_LIST_WITH_COMMA_SUPPORT' && (
        <PolicyEditorConstraintStringList
          policyEditor={policyEditor}
          treeNode={treeNode}
        />
      )}
    </>
  );
};
