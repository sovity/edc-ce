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
import {FetchAPI} from '@/lib/api/utils/client-utils';
import {
  getBody,
  getMethod,
  getQueryParams,
  getUrl,
} from '@/lib/api/utils/request-utils';
import {UrlInterceptor} from '@/lib/api/utils/url-interceptor';
import {noContent, notFound, ok} from '@/lib/api/utils/response-utils';
import {
  addCounterparty,
  deleteCounterparty,
  fakeCounterparties,
} from '@/lib/api/fake-backend/data/fake-counterparties';
import {CounterpartyAddDto} from '@/lib/api/models/counterparty-add-dto';
import {
  addMessageToConnector,
  getMessagesByConnectorId,
} from '@/lib/api/fake-backend/data/fake-messages';
import {MessageSendDto} from '@/lib/api/models/message-send-dto';
import {env} from '@/env';

export const FAKE_BACKEND: FetchAPI = async (
  input: RequestInfo,
  init?: RequestInit,
): Promise<Response> => {
  const url = getUrl(input, env.NEXT_PUBLIC_BACKEND_URL + '/');
  const method = getMethod(init);
  const body: unknown = getBody(init);
  const params = getQueryParams(input);

  console.log(
    ...[
      'Fake Backend:',
      method,
      url,
      params?.get('size') ? params : null,
      body,
    ].filter((it) => !!it),
  );

  return new UrlInterceptor(url, method)
    .url('counterparties')
    .on('GET', () => {
      return ok(fakeCounterparties);
    })

    .url('counterparties')
    .on('POST', () => {
      const counterpartyAddRequest = body as CounterpartyAddDto;
      return ok(addCounterparty(counterpartyAddRequest));
    })

    .url('counterparties/*')
    .on('DELETE', (participantId: string) => {
      if (deleteCounterparty(participantId)) {
        return noContent();
      } else {
        return notFound();
      }
    })

    .url('counterparties/*/messages')
    .on('GET', (connectorId: string) => {
      return ok(getMessagesByConnectorId(connectorId));
    })

    .url('counterparties/*/messages')
    .on('POST', (connectorId: string) => {
      const messageSendRequest = body as MessageSendDto;
      return ok(addMessageToConnector(connectorId, messageSendRequest));
    })

    .tryMatch();
};
