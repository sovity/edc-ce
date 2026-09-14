/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

/**
 * Runtime environment variables.
 *
 * `NEXT_PUBLIC_*` variables are normally inlined by Next.js at build time,
 * which would mean rebuilding the Docker image for every deployment. Instead
 * the server serialises them into the page at request time (see
 * `PublicEnvScript`) and the browser reads them back from `window.__ENV`.
 *
 * Plain JavaScript on purpose: `next.config.js` imports `src/env.js` directly
 * through Node, outside of the Next.js compiler, so anything in that import
 * chain has to be loadable by Node as-is.
 */

export const PUBLIC_ENV_KEY = '__ENV';

/**
 * Every `NEXT_PUBLIC_*` variable present in the server's environment.
 */
export function getPublicEnv() {
  return Object.keys(process.env)
    .filter((key) => key.startsWith('NEXT_PUBLIC_'))
    .reduce((acc, key) => ({...acc, [key]: process.env[key]}), {});
}

/**
 * Reads a public variable in the browser, or any variable on the server.
 *
 * @param {string} key
 * @returns {string | undefined}
 */
export function env(key) {
  const browserEnv =
    typeof window === 'undefined'
      ? undefined
      : /** @type {Record<string, Record<string, string | undefined>>} */ (
          /** @type {unknown} */ (window)
        )[PUBLIC_ENV_KEY];

  if (browserEnv) {
    if (!key.startsWith('NEXT_PUBLIC_')) {
      throw new Error(
        `Environment variable '${key}' is not public and cannot be read in the browser.`,
      );
    }
    return browserEnv[key];
  }

  return /** @type {Record<string, string | undefined>} */ (process.env)[key];
}
