/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {readArrayLiteral} from '@/components/policy-editor/core/policy-jsonld-utils';
import type {PolicyValueTypeAdapter} from '@/components/policy-editor/model/policy-value-type-adapter';
import {
  type PolicyEditorConstraintFormValue,
  type PolicyValueType,
} from '@/components/policy-editor/value-types/all';
import {filterNonNull} from '@/lib/utils/array-utils';
import {OperatorDto} from '@sovity.de/edc-client';
import {z} from 'zod';
import {type UsePolicyEditor} from '../editor/use-policy-editor';
import {type PolicyEditorNodeValue} from '../model/policy-editor-node-value';
import {type TreeNode} from '../core/tree-node';
import {useTranslations} from 'next-intl';
import {OperatorSelect} from '../editor/operator-select';
import {AsyncComboboxField} from '@/components/form/async-combobox-field';
import {api} from '@/lib/api/client';
import {queryKeys} from '@/lib/queryKeys';

export const inBusinessPartnerGroupFormSchema = z.object({
  type: z.literal('IN_BUSINESS_PARTNER_GROUP' satisfies PolicyValueType),
  operator: z.nativeEnum(OperatorDto),
  stringList: z.array(z.string()).min(1),
});

export type InBusinessPartnerGroupFormValue = z.infer<
  typeof inBusinessPartnerGroupFormSchema
>;

export const inBusinessPartnerGroupAdapter: PolicyValueTypeAdapter = {
  displayText: (literal): string | null => readArrayLiteral(literal).join(', '),
  buildFormValueFn: (literal, operator): PolicyEditorConstraintFormValue => {
    return {
      type: 'IN_BUSINESS_PARTNER_GROUP',
      operator,
      stringList: filterNonNull(readArrayLiteral(literal)),
    };
  },
  buildValueFn: (valuePoly) => {
    const value = valuePoly as InBusinessPartnerGroupFormValue;
    const items = value.stringList ?? [];

    return {
      type: 'STRING_LIST',
      valueList: items,
    };
  },
  emptyConstraintValue: () => ({
    operator: 'IS_ANY_OF',
    right: {
      type: 'STRING_LIST',
      valueList: [],
    },
  }),
};

export const PolicyEditorConstraintInBusinessPartnerGroup = ({
  policyEditor,
  treeNode,
}: {
  policyEditor: UsePolicyEditor;
  treeNode: TreeNode<PolicyEditorNodeValue>;
}) => {
  const t = useTranslations();
  const inBusinessPartnerGroupFormKey = policyEditor.formKeyForNode(
    treeNode,
    'inBusinessPartnerGroup',
  );

  return (
    <div className="flex gap-4">
      <OperatorSelect policyEditor={policyEditor} treeNode={treeNode} />
      <AsyncComboboxField
        multiselect
        className="grow"
        name={inBusinessPartnerGroupFormKey}
        control={policyEditor.form.control}
        buildQueryKey={(query) =>
          queryKeys.businessPartnerGroups.listPage(query)
        }
        label={
          treeNode.value.verb?.operandRightTitle?.(t) ?? t('General.unknown')
        }
        selectPlaceholder={
          treeNode.value.verb?.operandRightPlaceholder?.(t) ??
          t('General.unknown')
        }
        searchPlaceholder={t(
          'General.Policies.BusinessPartnerGroup.searchPlaceholder',
        )}
        loadItems={(query) =>
          api.uiApi
            .businessPartnerGroupListPage({
              businessPartnerGroupQuery: {
                searchQuery: query,
                limit: 10,
              },
            })
            .then((res) =>
              res.map((item) => ({
                id: item.groupId,
                label: item.groupId,
              })),
            )
        }
        isRequired={true}
      />
    </div>
  );
};
