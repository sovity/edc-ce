/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {z} from 'zod';

export const kebabCase = (str: z.ZodString) =>
  str.refine((value) => /^[a-z0-9]+(-[a-z0-9]+)*$/.test(value), {
    message:
      'Must only contain lowercase alphanumeric characters and "-". Must not start or end with "-".',
  });

export const optionalOrNonEmptyString = (optional: boolean) =>
  optional ? z.string().optional() : z.string().min(1);

export const jsonString = () =>
  z.string().refine(
    (it) => {
      try {
        JSON.parse(it);
        return true;
      } catch {
        return false;
      }
    },
    {message: 'Invalid JSON'},
  );

export const dateRequiredButNullable = () =>
  z
    .date()
    .nullable()
    .refine((value) => value !== null, {message: 'Required.'});
