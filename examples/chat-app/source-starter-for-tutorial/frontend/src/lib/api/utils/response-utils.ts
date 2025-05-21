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
export function ok(body: any): Promise<Response> {
  console.log('Fake Backend returns: ', body);
  return new Promise((resolve) => {
    const response = new Response(JSON.stringify(body), {status: 200});
    setTimeout(() => resolve(response), 400);
  });
}

export function notFound(): Promise<Response> {
  return new Promise((resolve) => {
    const response = new Response('Not Found', {status: 404});
    setTimeout(() => resolve(response), 400);
  });
}

export function noContent(): Promise<Response> {
  return new Promise((resolve) => {
    const response = new Response(null, {status: 204});
    setTimeout(() => resolve(response), 400);
  });
}
