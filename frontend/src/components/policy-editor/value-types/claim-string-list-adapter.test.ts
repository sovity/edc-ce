/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {describe, expect, test} from '@jest/globals';
import {
  claimNameFromLeftOperand,
  claimStringListAdapter,
  claimStringListFormSchema,
  type ClaimStringListFormValue,
  leftOperandFromClaimName,
} from './claim-string-list-adapter';

describe('claimStringListAdapter', () => {
  test('left operand <-> claim name', () => {
    expect(claimNameFromLeftOperand('POLICY_CLAIM_COUNTRY')).toBe('COUNTRY');
    expect(claimNameFromLeftOperand('POLICY_CLAIM_')).toBe('');
    expect(leftOperandFromClaimName('COUNTRY')).toBe('POLICY_CLAIM_COUNTRY');
    expect(leftOperandFromClaimName(' COUNTRY ')).toBe('POLICY_CLAIM_COUNTRY');
  });

  test('existing EQ constraint -> form value', () => {
    // act
    const actual = claimStringListAdapter.buildFormValueFn(
      {type: 'STRING', value: 'DE, AT'},
      'EQ',
      'POLICY_CLAIM_COUNTRY',
    ) as ClaimStringListFormValue;

    // assert
    expect(actual).toEqual({
      type: 'CLAIM_STRING_LIST',
      operator: 'EQ',
      claimName: 'COUNTRY',
      stringList: ['DE', 'AT'],
    });
  });

  test('existing IN constraint -> form value', () => {
    // act
    const actual = claimStringListAdapter.buildFormValueFn(
      {type: 'STRING_LIST', valueList: ['DE', 'AT']},
      'IN',
      'POLICY_CLAIM_COUNTRY',
    ) as ClaimStringListFormValue;

    // assert
    expect(actual).toEqual({
      type: 'CLAIM_STRING_LIST',
      operator: 'IN',
      claimName: 'COUNTRY',
      stringList: ['DE', 'AT'],
    });
  });

  test('newly added constraint -> empty form value', () => {
    // arrange
    const empty = claimStringListAdapter.emptyConstraintValue();

    // act
    const actual = claimStringListAdapter.buildFormValueFn(
      empty.right,
      empty.operator,
      'POLICY_CLAIM_',
    ) as ClaimStringListFormValue;

    // assert
    expect(actual).toEqual({
      type: 'CLAIM_STRING_LIST',
      operator: 'IN',
      claimName: '',
      stringList: [],
    });
  });

  test('form value with EQ -> comma separated string literal + left operand', () => {
    // arrange
    const formValue: ClaimStringListFormValue = {
      type: 'CLAIM_STRING_LIST',
      operator: 'EQ',
      claimName: 'COUNTRY',
      stringList: ['DE', 'AT'],
    };

    // act
    const left = claimStringListAdapter.buildLeftFn!(formValue);
    const right = claimStringListAdapter.buildValueFn(formValue);

    // assert
    expect(left).toBe('POLICY_CLAIM_COUNTRY');
    expect(right).toEqual({type: 'STRING', value: 'DE,AT'});
  });

  test('form value with IN -> string list literal', () => {
    // arrange
    const formValue: ClaimStringListFormValue = {
      type: 'CLAIM_STRING_LIST',
      operator: 'IN',
      claimName: 'COUNTRY',
      stringList: ['DE', 'AT'],
    };

    // act
    const right = claimStringListAdapter.buildValueFn(formValue);

    // assert
    expect(right).toEqual({type: 'STRING_LIST', valueList: ['DE', 'AT']});
  });

  test('form schema rejects empty or invalid claim names', () => {
    const valid = {
      type: 'CLAIM_STRING_LIST',
      operator: 'EQ',
      claimName: 'COUNTRY',
      stringList: ['DE'],
    };

    expect(claimStringListFormSchema.safeParse(valid).success).toBe(true);
    expect(
      claimStringListFormSchema.safeParse({...valid, claimName: ''}).success,
    ).toBe(false);
    expect(
      claimStringListFormSchema.safeParse({...valid, claimName: 'my claim'})
        .success,
    ).toBe(false);
    expect(
      claimStringListFormSchema.safeParse({...valid, stringList: []}).success,
    ).toBe(false);
  });
});
