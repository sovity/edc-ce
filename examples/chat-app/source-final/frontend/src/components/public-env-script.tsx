/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
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
