/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {describe, expect, test} from '@jest/globals';
import {getInitials} from './string-utils';

describe('getInitials', () => {
  test('only firstname', () => {
    // arrange
    const name = 'ab';

    // act
    const actual = getInitials(name);

    // assert
    expect(actual).toBe('AB');
  });

  test('multiple names', () => {
    // arrange
    const name = 'ab cd ef';

    // act
    const actual = getInitials(name);

    // assert
    expect(actual).toBe('AE');
  });

  test('empty', () => {
    // arrange
    const name = '';

    // act
    const actual = getInitials(name);

    // assert
    expect(actual).toBe('');
  });
});
