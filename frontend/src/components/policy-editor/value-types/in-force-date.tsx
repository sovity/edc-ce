/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  readJsonLiteral,
  readSingleStringLiteral,
} from '@/components/policy-editor/core/policy-jsonld-utils';
import type {PolicyValueTypeAdapter} from '@/components/policy-editor/model/policy-value-type-adapter';
import {
  type PolicyEditorConstraintFormValue,
  type PolicyValueType,
} from '@/components/policy-editor/value-types/all';
import {OperatorDto} from '@sovity.de/edc-client';
import {z} from 'zod';
import type {UsePolicyEditor} from '@/components/policy-editor/editor/use-policy-editor';
import type {TreeNode} from '@/components/policy-editor/core/tree-node';
import type {PolicyEditorNodeValue} from '@/components/policy-editor/model/policy-editor-node-value';
import {useTranslations} from 'next-intl';
import {OperatorSelect} from '@/components/policy-editor/editor/operator-select';
import InputField from '@/components/form/input-field';
import {DateInput} from '@/components/ui/date-input';
import {HourglassIcon} from 'lucide-react';
import {Button} from '@/components/ui/button';
import * as React from 'react';

export const inForceDateSchema = z.object({
  type: z.literal('IN_FORCE_DATE' satisfies PolicyValueType),
  operator: z.nativeEnum(OperatorDto),
  inForceDate: z.union([
    z
      .string()
      .datetime(
        "Input is not a valid ISO date string or 'contractAgreement+<number><s|m|h|d>'.",
      ),
    z
      .string()
      .regex(
        /(contract[A,a]greement)\+(-?[0-9]+)(s|m|h|d)/,
        "Input is not a valid ISO date string or 'contractAgreement+<number><s|m|h|d>'.",
      ),
  ]),
});

export type InForceDateFormValue = z.infer<typeof inForceDateSchema>;

export const inForceDateAdapter: PolicyValueTypeAdapter = {
  displayText: (literal) => readJsonLiteral(literal),
  buildFormValueFn: (literal, operator): PolicyEditorConstraintFormValue => ({
    type: 'IN_FORCE_DATE',
    operator,
    inForceDate: readSingleStringLiteral(literal) ?? '',
  }),
  buildValueFn: (valuePoly) => {
    const value = valuePoly as InForceDateFormValue;
    return {
      type: 'STRING',
      value: value.inForceDate,
    };
  },
  emptyConstraintValue: () => ({
    operator: 'LT',
    right: {
      type: 'STRING',
      value: '',
    },
  }),
};

export const PolicyEditorConstraintInForceDate = ({
  policyEditor,
  treeNode,
}: {
  policyEditor: UsePolicyEditor;
  treeNode: TreeNode<PolicyEditorNodeValue>;
}) => {
  const t = useTranslations();
  const inForceDateFormKey = policyEditor.formKeyForNode(
    treeNode,
    'inForceDate',
  );

  const setValue = (value: string) => {
    policyEditor.form.setValue(inForceDateFormKey, value, {
      shouldValidate: true,
      shouldTouch: true,
      shouldDirty: true,
    });
  };

  return (
    <div className={'flex gap-4'}>
      <OperatorSelect policyEditor={policyEditor} treeNode={treeNode} />
      <div className={'flex flex-col gap-2'}>
        <InputField
          name={inForceDateFormKey}
          control={policyEditor.form.control}
          placeholder={treeNode.value.verb?.operandRightPlaceholder?.(t)}
          label={treeNode.value.verb?.operandRightTitle?.(t) ?? 'Unknown'}
          isRequired={true}
        />

        <div className={'flex gap-4'}>
          <DateInput
            dataTestId={`${inForceDateFormKey}-pick-date`}
            date={null}
            setDate={(date) => setValue(date ? date.toISOString() : '')}
            className={'flex grow'}
          />
          <Button
            dataTestId={`${inForceDateFormKey}-pick-30d`}
            variant={'outline'}
            className={
              'flex justify-start gap-2 text-left font-normal text-muted-foreground'
            }
            onClick={(e) => {
              setValue('contractAgreement+30d');
              e.preventDefault();
            }}>
            <HourglassIcon />
            <span>{t('General.Policies.Verbs.inForceDateForm.30days')}</span>
          </Button>
          <Button
            dataTestId={`${inForceDateFormKey}-pick-180d`}
            variant={'outline'}
            className={
              'flex justify-start gap-2 text-left font-normal text-muted-foreground'
            }
            onClick={(e) => {
              setValue('contractAgreement+180d');
              e.preventDefault();
            }}>
            <HourglassIcon />
            <span>{t('General.Policies.Verbs.inForceDateForm.6months')}</span>
          </Button>
        </div>
      </div>
    </div>
  );
};
