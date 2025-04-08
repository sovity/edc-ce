/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
/**
 * Similar to "keyof T" but now you can say "KeysOfType<T, WithThisType>".
 */
export type KeysOfType<O, T> = {
  [K in keyof O]: O[K] extends T ? K : never;
}[keyof O];

export function isValueOfEnum<T>(
  enumObj: {[key in keyof T]: T[key]},
  value: any,
): value is T[keyof T] {
  return Object.values(enumObj).includes(value);
}
