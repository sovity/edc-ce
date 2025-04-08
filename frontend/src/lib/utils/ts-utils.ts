/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
export function unsafeCast<T>(obj: unknown): T {
  // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
  return obj as T;
}

export function isNotNull<T>(value: T | null): value is T {
  return value !== null;
}
