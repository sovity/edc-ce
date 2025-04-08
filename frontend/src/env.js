/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-nocheck
import {createEnv} from '@t3-oss/env-nextjs';
import {env as runtimeEnv} from 'next-runtime-env';
import {z} from 'zod';

export const env = createEnv({
  /**
   * Specify your server-side environment variables schema here. This way you can ensure the app
   * isn't built with invalid env vars.
   */
  server: {
    NODE_ENV: z
      .enum(['development', 'test', 'production'])
      .default('development'),
  },

  /**
   * Specify your client-side environment variables schema here. This way you can ensure the app
   * isn't built with invalid env vars. To expose them to the client, prefix them with
   * `NEXT_PUBLIC_`.
   */
  client: {
    NEXT_PUBLIC_USE_FAKE_BACKEND: z
      .enum(['true', 'false'])
      .transform((v) => v === 'true'),
    NEXT_PUBLIC_MANAGEMENT_API_URL: z
      .string()
      .refine((value) => /^(https?:\/)?\/\S+$/i.exec(value), {
        message: "Must be either a URL or start with '/'",
      }),
    NEXT_PUBLIC_MANAGEMENT_API_KEY: z.string().optional(),
    NEXT_PUBLIC_BUILD_VERSION: z.string().default('unknown'),
    NEXT_PUBLIC_BUILD_DATE: z.string().default('unknown'),
    NEXT_PUBLIC_DEVTOOLS_ENABLED: z
      .enum(['true', 'false'])
      .transform((v) => v === 'true'),
  },

  /**
   * You can't destruct `process.env` as a regular object in the Next.js edge runtimes (e.g.
   * middlewares) or client-side so we need to destruct manually.
   */
  runtimeEnv: {
    NODE_ENV: process.env.NODE_ENV,
    NEXT_PUBLIC_USE_FAKE_BACKEND: runtimeEnv('NEXT_PUBLIC_USE_FAKE_BACKEND'),
    NEXT_PUBLIC_MANAGEMENT_API_URL: runtimeEnv(
      'NEXT_PUBLIC_MANAGEMENT_API_URL',
    ),
    NEXT_PUBLIC_MANAGEMENT_API_KEY: runtimeEnv(
      'NEXT_PUBLIC_MANAGEMENT_API_KEY',
    ),
    NEXT_PUBLIC_BUILD_VERSION: runtimeEnv('NEXT_PUBLIC_BUILD_VERSION'),
    NEXT_PUBLIC_BUILD_DATE: runtimeEnv('NEXT_PUBLIC_BUILD_DATE'),
    NEXT_PUBLIC_DEVTOOLS_ENABLED: runtimeEnv('NEXT_PUBLIC_DEVTOOLS_ENABLED'),
  },
  /**
   * Run `build` or `dev` with `SKIP_ENV_VALIDATION` to skip env validation. This is especially
   * useful for Docker builds.
   */
  skipValidation: !!process.env.SKIP_ENV_VALIDATION,
  /**
   * Makes it so that empty strings are treated as undefined. `SOME_VAR: z.string()` and
   * `SOME_VAR=''` will throw an error.
   */
  emptyStringAsUndefined: true,
});
