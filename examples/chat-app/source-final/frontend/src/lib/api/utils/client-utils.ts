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
import {env} from '@/env';
import {FAKE_BACKEND} from '@/lib/api/fake-backend/fake-backend';

export type FetchAPI = WindowOrWorkerGlobalScope['fetch'];

const shouldUseFakeBackend = env.NEXT_PUBLIC_USE_FAKE_BACKEND;
const backendBaseUrl = env.NEXT_PUBLIC_BACKEND_URL;

export const request = async <T, R>(
  method: string,
  url: string,
  body?: T,
): Promise<R> => {
  const fetchApi: FetchAPI = shouldUseFakeBackend ? FAKE_BACKEND : fetch;

  const response = await fetchApi(`${backendBaseUrl}/${url}`, {
    method,
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
    },
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {
    throw new Error('Failed to fetch');
  }

  if (response.body === null) {
    return {} as R;
  }

  return (await response.json()) as R;
};
