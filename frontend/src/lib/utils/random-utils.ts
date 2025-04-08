/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
export function getRandomIntegerInRange(a: number, b: number): number {
  if (a > b) {
    throw new Error(
      'First parameter must be less than or equal to second parameter',
    );
  }

  return ~~(Math.random() * (b - a + 1)) + a;
}
