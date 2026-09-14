/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

import {PUBLIC_ENV_KEY, getPublicEnv} from '@/lib/runtime-env';
import {connection} from 'next/server';

/**
 * Serialises the server's `NEXT_PUBLIC_*` variables into the document so the
 * browser can read them at runtime rather than having them inlined at build
 * time. Render it inside `<head>`.
 *
 * `await connection()` excludes this component from prerendering, so it runs
 * per request even in an otherwise static page. Without it, `next build`
 * would bake the build environment's (usually empty) variables into the HTML
 * and silently ignore the container's runtime configuration.
 */
export async function PublicEnvScript() {
  await connection();

  // `</script>` inside a value would otherwise close the tag early, so escape
  // the angle brackets. JSON.stringify alone is not enough here.
  const serialized = JSON.stringify(getPublicEnv())
    .replace(/</g, '\\u003c')
    .replace(/>/g, '\\u003e');

  return (
    <script
      dangerouslySetInnerHTML={{
        __html: `window[${JSON.stringify(PUBLIC_ENV_KEY)}] = ${serialized}`,
      }}
    />
  );
}
