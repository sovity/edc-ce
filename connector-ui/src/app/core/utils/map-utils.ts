/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
/**
 * Group items by key extractor
 * @param array items
 * @param keyExtractor key extractor
 */
export function groupedBy<T, K>(
  array: T[],
  keyExtractor: (it: T) => K,
): Map<K, T[]> {
  const map = new Map<K, T[]>();
  array.forEach((it) => {
    const key = keyExtractor(it);
    if (!map.has(key)) {
      map.set(key, []);
    }
    map.get(key)!.push(it);
  });
  return map;
}

/**
 * Create Map with entries [keyExtractor(it), it]
 * @param array items
 * @param keyExtractor key extractor
 */
export function associateBy<T, K>(
  array: T[],
  keyExtractor: (it: T) => K,
): Map<K, T> {
  return new Map(array.map((it) => [keyExtractor(it), it]));
}
