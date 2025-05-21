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
import {MessageDto} from '@/lib/api/models/message-dto';
import {MessageDirectionDto} from '@/lib/api/models/message-direction-dto';
import {MessageStatusDto} from '@/lib/api/models/message-status-dto';
import {MessageSendDto} from '@/lib/api/models/message-send-dto';
import {v4 as uuidv4} from 'uuid';
import {ConnectionStatusDto} from '@/lib/api/models/connection-status-dto';
import {fakeCounterparties} from '@/lib/api/fake-backend/data/fake-counterparties';

type ConnectorMessagesMap = Record<string, MessageDto[]>;

const fakeMessages: ConnectorMessagesMap = {
  'connector-1': [
    {
      messageId: '123e4567-e89b-12d3-a456-426614174000',
      createdAt: new Date('2023-05-10T14:30:00Z'),
      message: 'Hello from connector 1',
      messageDirection: MessageDirectionDto.INCOMING,
      status: MessageStatusDto.OK,
    },
    {
      messageId: '123e4567-e89b-12d3-a456-426614174001',
      createdAt: new Date('2023-05-10T14:35:00Z'),
      message: 'Reply to connector 1',
      messageDirection: MessageDirectionDto.OUTGOING,
      status: MessageStatusDto.OK,
    },
  ],

  'connector-2': [
    {
      messageId: '123e4567-e89b-12d3-a456-426614174002',
      createdAt: new Date('2023-05-11T09:15:00Z'),
      message: 'Message from connector 2',
      messageDirection: MessageDirectionDto.INCOMING,
      status: MessageStatusDto.OK,
    },
  ],

  'connector-3': [],
};

export const getMessagesByConnectorId = (connectorId: string): MessageDto[] => {
  return fakeMessages[connectorId] ?? [];
};

export const addMessageToConnector = (
  connectorId: string,
  messageSend: MessageSendDto,
): MessageDto => {
  if (!fakeMessages[connectorId]) {
    fakeMessages[connectorId] = [];
  }

  const newMessage: MessageDto = {
    messageId: uuidv4(),
    createdAt: new Date(),
    message: messageSend.message,
    messageDirection: MessageDirectionDto.OUTGOING,
    status: MessageStatusDto.SENDING,
  };

  const index = fakeMessages[connectorId].push(newMessage) - 1;

  setTimeout(() => {
    fakeMessages[connectorId][index].status = MessageStatusDto.OK;
  }, 5000);

  return newMessage;
};

export default fakeMessages;
