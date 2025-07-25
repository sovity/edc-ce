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
  rawJsonSchema,
} from '@/components/policy-editor/value-types/raw-json';
import {
  stringListWithCommaSupportAdapter,
  stringListWithCommaSupportFormSchema,
} from '@/components/policy-editor/value-types/string-list-with-comma-support';
import {z} from 'zod';
import {
  inForceDateAdapter,
  inForceDateSchema,
  PolicyEditorConstraintInForceDate,
} from '@/components/policy-editor/value-types/in-force-date';
import {
  stringListCatenaStyleAdapter,
  stringListCatenaStyleFormSchema,
} from '@/components/policy-editor/value-types/string-list-catena-style';
import {PolicyEditorConstraintStringList} from '@/components/policy-editor/value-types/string-list-common';
import {
  PolicyEditorConstraintString,
  stringAdapter,
  stringFormSchema,
} from '@/components/policy-editor/value-types/string';
import {
  inBusinessPartnerGroupAdapter,
  inBusinessPartnerGroupFormSchema,
  PolicyEditorConstraintInBusinessPartnerGroup,
} from './in-business-partner-group';

export type PolicyValueType =
  | 'RAW_JSON'
  | 'STRING'
  | 'DATETIME_TRUNCATE_TO_DATE'
  | 'IN_FORCE_DATE'
  | 'STRING_LIST_WITH_COMMA_SUPPORT'
  | 'STRING_LIST_CATENA_STYLE'
  | 'IN_BUSINESS_PARTNER_GROUP';

/**
 * All known policy value types
 */
export const policyValueTypeAdaptersById: Record<
  PolicyValueType,
  PolicyValueTypeAdapter
> = {
  RAW_JSON: rawJsonAdapter,
  STRING: stringAdapter,
  DATETIME_TRUNCATE_TO_DATE: datetimeTruncateToDateAdapter,
  IN_FORCE_DATE: inForceDateAdapter,
  STRING_LIST_WITH_COMMA_SUPPORT: stringListWithCommaSupportAdapter,
  STRING_LIST_CATENA_STYLE: stringListCatenaStyleAdapter,
  IN_BUSINESS_PARTNER_GROUP: inBusinessPartnerGroupAdapter,
};

/**
 * Discriminated Union for a Policy Editor Constraint Node
 */
export const policyEditorConstraintFormSchema = z.discriminatedUnion('type', [
  rawJsonSchema,
  stringFormSchema,
  datetimeTruncateToDateFormSchema,
  inForceDateSchema,
  stringListCatenaStyleFormSchema,
  stringListWithCommaSupportFormSchema,
  inBusinessPartnerGroupFormSchema,
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
      {treeNode.value.verb?.valueType === 'STRING' && (
        <PolicyEditorConstraintString
          policyEditor={policyEditor}
          treeNode={treeNode}
        />
      )}
      {treeNode.value.verb?.valueType === 'DATETIME_TRUNCATE_TO_DATE' && (
        <PolicyEditorConstraintDate
          policyEditor={policyEditor}
          treeNode={treeNode}
        />
      )}
      {treeNode.value.verb?.valueType === 'IN_FORCE_DATE' && (
        <PolicyEditorConstraintInForceDate
          policyEditor={policyEditor}
          treeNode={treeNode}
        />
      )}
      {(treeNode.value.verb?.valueType === 'STRING_LIST_WITH_COMMA_SUPPORT' ||
        treeNode.value.verb?.valueType === 'STRING_LIST_CATENA_STYLE') && (
        <PolicyEditorConstraintStringList
          policyEditor={policyEditor}
          treeNode={treeNode}
        />
      )}
      {treeNode.value.verb?.valueType === 'IN_BUSINESS_PARTNER_GROUP' && (
        <PolicyEditorConstraintInBusinessPartnerGroup
          policyEditor={policyEditor}
          treeNode={treeNode}
        />
      )}
    </>
  );
};
