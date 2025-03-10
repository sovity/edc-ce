/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
/**
 * Remove fields with null values from Property Records due to EDC Backend expecting non-null values
 * @param obj object / record
 */
export function removeNullValues(
  obj: Record<string, string | null>,
): Record<string, string> {
  return Object.fromEntries(
    Object.entries(obj).filter(([_, v]) => v != null) as [string, string][],
  );
}

/**
 * Remove fields with undefined values from property records
 * @param obj object / record
 */
export function removeUndefinedValues(
  obj: Record<string, string | undefined>,
): Record<string, string> {
  return Object.fromEntries(
    Object.entries(obj).filter(([_, v]) => v != null) as [string, string][],
  );
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
