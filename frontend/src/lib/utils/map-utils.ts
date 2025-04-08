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

/**
 * Create Map with entries [keyExtractor(it), it]
 * @param array items
 * @param keyExtractor key extractor
 */
export function associateByR<T>(
  array: T[],
  keyExtractor: (it: T) => string,
): Record<string, T> {
  return Object.fromEntries(array.map((it) => [keyExtractor(it), it]));
}

/**
 * Maps keys of a given object
 * @param obj object
 * @param mapFn key mapper
 * @return new object with keys mapped
 */
export function mapKeys<
  K extends string | number | symbol,
  L extends string | number | symbol,
  V,
>(obj: Record<K, V>, mapFn: (key: K) => L): Record<L, V> {
  return Object.fromEntries(
    Object.entries(obj).map(([k, v]) => [mapFn(k as K), v]),
  ) as Record<L, V>;
}

/**
 * Remove fields with null values from Property Records due to EDC Backend expecting non-null values
 * @param obj object / record
 */
export function removeNullValues(
  obj: Record<string, string | null | undefined>,
): Record<string, string> {
  return Object.fromEntries(
    Object.entries(obj).filter(([_, v]) => v != null) as [string, string][],
  );
}
