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
'use client';

import {CounterpartyDto} from '@/lib/api/models/counterparty-dto';
import {request} from '@/lib/api/utils/client-utils';
import {CounterpartyAddDto} from '@/lib/api/models/counterparty-add-dto';
import {MessageDto} from '@/lib/api/models/message-dto';
import {MessageSendDto} from '@/lib/api/models/message-send-dto';

const e = encodeURIComponent;

export const listCounterparties = async (): Promise<CounterpartyDto[]> => {
  return await request('GET', 'counterparties');
};

export const addCounterparty = async (
  requestBody: CounterpartyAddDto,
): Promise<CounterpartyDto> => {
  return await request('POST', 'counterparties', requestBody);
};

export const deleteCounterparty = async (
  participantId: string,
): Promise<void> => {
  return await request('DELETE', `counterparties/${e(participantId)}`);
};

export const getAllMessages = async (
  participantId: string,
): Promise<MessageDto[]> => {
  return await request('GET', `counterparties/${e(participantId)}/messages`);
};

export const sendMessage = async (
  participantId: string,
  messageSendRequest: MessageSendDto,
): Promise<MessageDto> => {
  return await request(
    'POST',
    `counterparties/${e(participantId)}/messages`,
    messageSendRequest,
  );
};
