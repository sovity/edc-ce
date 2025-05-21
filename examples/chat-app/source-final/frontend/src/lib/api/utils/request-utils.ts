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
export const getUrl = (input: Request | string, baseUrl: string): string => {
  const url = new URL(typeof input === 'string' ? input : input.url);
  const urlNoQuery = url.origin + url.pathname;
  return urlNoQuery.startsWith(baseUrl)
    ? urlNoQuery.substring(baseUrl.length)
    : urlNoQuery;
};

export const getMethod = (init: RequestInit | undefined): string =>
  init?.method ?? 'GET';

export const getBody = (input: RequestInit | undefined): unknown => {
  // eslint-disable-next-line @typescript-eslint/no-base-to-string
  const body = input?.body?.toString();
  return body ? JSON.parse(body) : null;
};

export const getQueryParams = (input: Request | string): URLSearchParams => {
  const url = new URL(typeof input === 'string' ? input : input.url);
  return url.searchParams;
};
