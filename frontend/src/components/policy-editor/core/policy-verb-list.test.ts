/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {describe, expect, test} from '@jest/globals';
import {PolicyVerbList} from './policy-verb-list';
import {type PolicyVerbConfig} from '@/components/policy-editor/model/policy-verb-config';
import {type PolicyOperatorConfig} from '@/components/policy-editor/model/policy-operator-config';
import {byFallback} from '@/lib/utils/translation-utils';

const referringConnector: PolicyVerbConfig = {
  operandLeftIds: ['REFERRING_CONNECTOR'],
  operandLeftTitle: byFallback('Referring Connector'),
  operandLeftDescription: byFallback(''),
  supportedOperators: ['EQ', 'IN'],
  valueType: 'STRING_LIST_WITH_COMMA_SUPPORT',
};

const genericClaim: PolicyVerbConfig = {
  operandLeftIds: ['POLICY_CLAIM_'],
  operandLeftPrefix: 'POLICY_CLAIM_',
  operandLeftTitle: byFallback('Generic Claim'),
  operandLeftDescription: byFallback(''),
  supportedOperators: ['EQ', 'IN'],
  valueType: 'CLAIM_STRING_LIST',
};

const operators: PolicyOperatorConfig[] = [
  {id: 'EQ', title: byFallback('='), description: byFallback('equals')},
  {id: 'IN', title: byFallback('in'), description: byFallback('in')},
];

describe('PolicyVerbList', () => {
  const verbList = new PolicyVerbList(
    [referringConnector, genericClaim],
    operators,
  );

  test('exact id match', () => {
    // act
    const actual = verbList.getVerbConfig('REFERRING_CONNECTOR');

    // assert
    expect(actual).toBe(referringConnector);
  });

  test('prefix match for dynamic left operand', () => {
    // act
    const actual = verbList.getVerbConfig('POLICY_CLAIM_COUNTRY');

    // assert
    expect(actual).toBe(genericClaim);
  });

  test('bare prefix matches the dynamic verb', () => {
    // act
    const actual = verbList.getVerbConfig('POLICY_CLAIM_');

    // assert
    expect(actual).toBe(genericClaim);
  });

  test('unknown verb falls back to raw json', () => {
    // act
    const actual = verbList.getVerbConfig('SOMETHING_ELSE');

    // assert
    expect(actual.valueType).toBe('RAW_JSON');
    expect(actual.operandLeftIds).toEqual(['SOMETHING_ELSE']);
    expect(actual.supportedOperators).toEqual(['EQ', 'IN']);
  });
});
