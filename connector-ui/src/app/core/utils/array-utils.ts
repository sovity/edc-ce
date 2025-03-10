/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
/**
 * Remove item once from list.
 *
 * Use this over .filter(...) to remove items on user interactions
 * to prevent one click from removing many items.
 *
 * Returns copy.
 */
export function removeOnce<T>(list: T[], item: T): T[] {
  const index = list.indexOf(item);
  if (index >= 0) {
    const copy = [...list];
    copy.splice(index, 1);
    return copy;
  }
  return list;
}

export function filterNonNull<T>(array: (T | null | undefined)[]): T[] {
  return array.filter((it) => it != null) as T[];
}
