/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {z} from 'zod';

/**
 * Extracts all keys from the given object.
 *
 * Useful for our dynamic react form generation.
 */
export const zodKeys = <T extends z.ZodTypeAny>(schema: T): string[] => {
  if (schema === null || schema === undefined) return [];
  if (schema instanceof z.ZodNullable || schema instanceof z.ZodOptional) {
    return zodKeys(schema.unwrap());
  }
  if (schema instanceof z.ZodArray) return zodKeys(schema.element);
  if (schema instanceof z.ZodObject) {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-argument
    return Object.entries(schema.shape).flatMap(([key, value]) => {
      // noinspection SuspiciousTypeOfGuard
      const nested =
        value instanceof z.ZodType
          ? zodKeys(value).map((subKey) => `${key}.${subKey}`)
          : [];
      return nested.length ? nested : key;
    });
  }
  if (
    schema instanceof z.ZodUnion ||
    schema instanceof z.ZodDiscriminatedUnion
  ) {
    return [...new Set((schema.options as z.ZodTypeAny[]).flatMap(zodKeys))];
  }
  return [];
};
