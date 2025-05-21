/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

/**
 * Transform flat keys to nested structure
 */
export const unflattenRecord = (
  flatMessages: Record<string, string>,
  separator: string,
): Record<string, unknown> => {
  const nested: Record<string, unknown> = {};
  Object.entries(flatMessages).forEach(([key, value]) => {
    const parts = key.split(separator);
    const property = parts.pop()!;

    let current = nested;
    parts.forEach((part) => {
      if (!current.hasOwnProperty(part)) {
        current[part] = {};
      }
      current = current[part] as Record<string, unknown>;
    });

    current[property] = value;
  });
  return nested;
};
