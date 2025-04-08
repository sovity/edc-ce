/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
export function filterHasValue<T>(array: (T | null | undefined)[]): T[] {
  return array.filter((it) => it !== null && it !== undefined) as T[];
}

export function getRandomElement<T>(arr: T[] | readonly T[]): T {
  if (arr.length === 0) {
    throw new Error('Cannot get random element from an empty array');
  }
  const randomIndex = ~~(Math.random() * arr.length);
  return arr[randomIndex]!;
}

export function getUniqueValues<T>(
  array: T[],
  keyExtractor: (item: T) => string,
): string[] {
  return Array.from(new Set(array.map(keyExtractor)));
}

export const filterNonNull = <T>(array: (T | null | undefined)[]): T[] =>
  array.filter((it) => it != null) as T[];
