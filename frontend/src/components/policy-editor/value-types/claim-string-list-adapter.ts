/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import type {PolicyValueTypeAdapter} from '@/components/policy-editor/model/policy-value-type-adapter';
import {
  type PolicyEditorConstraintFormValue,
  type PolicyValueType,
} from '@/components/policy-editor/value-types/all';
import {
  stringListWithCommaSupportAdapter,
  type StringListWithCommaSupportFormValue,
} from '@/components/policy-editor/value-types/string-list-with-comma-support';
import {OperatorDto} from '@sovity.de/edc-client';
import {z} from 'zod';

/**
 * Left operand prefix of the backend's GenericClaimsPolicyExtension.
 * A constraint "POLICY_CLAIM_COUNTRY EQ DE" is evaluated against the claim "COUNTRY" of the consumer's identity token.
 */
export const GENERIC_CLAIM_POLICY_PREFIX = 'POLICY_CLAIM_';

export const claimStringListFormSchema = z.object({
  type: z.literal('CLAIM_STRING_LIST' satisfies PolicyValueType),
  operator: z.nativeEnum(OperatorDto),
  // Claim names are looked up case-sensitively in the backend, so no normalization happens here
  claimName: z
    .string()
    .trim()
    .min(1)
    .regex(/^[A-Za-z0-9_.:-]+$/, {
      message: 'Only letters, digits and _ . : - are allowed',
    }),
  stringList: z.array(z.string()).min(1),
});

export type ClaimStringListFormValue = z.infer<
  typeof claimStringListFormSchema
>;

/**
 * "POLICY_CLAIM_COUNTRY" -> "COUNTRY", "POLICY_CLAIM_" -> ""
 */
export const claimNameFromLeftOperand = (left: string): string =>
  left.startsWith(GENERIC_CLAIM_POLICY_PREFIX)
    ? left.substring(GENERIC_CLAIM_POLICY_PREFIX.length)
    : left;

/**
 * "COUNTRY" -> "POLICY_CLAIM_COUNTRY"
 */
export const leftOperandFromClaimName = (claimName: string): string =>
  GENERIC_CLAIM_POLICY_PREFIX + claimName.trim();

/**
 * Generic claim policy: dynamic left operand + string list right operand.
 *
 * The right operand behaves exactly like STRING_LIST_WITH_COMMA_SUPPORT, since the backend parses the right operand
 * comma separated and lets EQ behave like IN.
 */
export const claimStringListAdapter: PolicyValueTypeAdapter = {
  displayText: stringListWithCommaSupportAdapter.displayText,
  buildFormValueFn: (
    literal,
    operator,
    left,
  ): PolicyEditorConstraintFormValue => {
    const right = stringListWithCommaSupportAdapter.buildFormValueFn(
      literal,
      operator,
      left,
    ) as StringListWithCommaSupportFormValue;
    return {
      type: 'CLAIM_STRING_LIST',
      operator,
      claimName: claimNameFromLeftOperand(left),
      stringList: right.stringList,
    };
  },
  buildValueFn: stringListWithCommaSupportAdapter.buildValueFn,
  buildLeftFn: (valuePoly) => {
    const value = valuePoly as ClaimStringListFormValue;
    return leftOperandFromClaimName(value.claimName);
  },
  emptyConstraintValue: () => ({
    operator: 'IN',
    right: {
      type: 'STRING_LIST',
      valueList: [],
    },
  }),
};
