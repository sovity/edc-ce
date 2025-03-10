/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import cleanDeep from 'clean-deep';
import jsonStableStringify from 'json-stable-stringify';

/**
 * Sorts keys, sorts array values, removes emtpy keys.
 *
 * @param json any JSON object
 */
export function cleanJson<T>(json: T): Partial<T> {
  const cleaned = cleanDeep(json, {emptyStrings: false});
  return JSON.parse(jsonStableStringify(cleaned));
}
