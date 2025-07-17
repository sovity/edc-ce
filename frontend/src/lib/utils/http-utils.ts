/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

export function decodeParams<T extends Record<string, string>>(params: T): T {
  return Object.fromEntries(
    Object.entries(params).map(([key, value]) => [
      key,
      decodeParam(value || ''),
    ]),
  ) as T;
}

/**
 * Encodes a URL parameter, preventing wrong decoding of URLs by next.js
 *
 * Next.js usually auto-decodes %2F to /, breaking URLs
 */
export function encodeParam(param: string): string {
  return encodeURIComponent(param)
    .replace(/~/g, '%7E') // forcibly encode "~", otherwise a free character in URLs
    .replace(/%/g, '~'); // replace "%" with "~"
}

/**
 * Decodes a URL parameter, handling special encoding by {@link encodeParam}
 */
export function decodeParam(param: string): string {
  return decodeURIComponent(param.replace(/~/g, '%'));
}
