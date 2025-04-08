/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
export type PatchFn<T> = (item: T) => Partial<T>;

/**
 * Patches an item of an array.
 *
 * @param items array
 * @param keyFn key extractor
 * @param key key of the item to patch
 * @param patchFn patcher
 * @returns new array
 */
export const patchItemByKey = <T, K>(
  items: T[],
  keyFn: (it: T) => K,
  key: K,
  patchFn: PatchFn<T>,
) => items.map((it) => (keyFn(it) === key ? {...it, ...patchFn(it)} : it));
