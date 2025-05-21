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
import type {Chat} from '@/types/chat';

export const mockChats: Chat[] = [
  {
    id: 'chat-1',
    name: 'Supply Chain Updates',
    messages: [
      {
        id: 'msg-1',
        content: 'Hello, I need information about our latest shipment status.',
        sender: 'user',
        timestamp: '2023-05-10T09:30:00Z',
        status: 'delivered',
      },
      {
        id: 'msg-2',
        content:
          'The shipment #CX-45678 is currently in transit and expected to arrive on May 15th.',
        sender: 'system',
        timestamp: '2023-05-10T09:31:00Z',
        status: 'delivered',
      },
      {
        id: 'msg-3',
        content: 'Are there any delays reported?',
        sender: 'user',
        timestamp: '2023-05-10T09:32:00Z',
        status: 'delivered',
      },
      {
        id: 'msg-4',
        content:
          'No delays have been reported. The shipment is on schedule according to the Catena-X tracking system.',
        sender: 'system',
        timestamp: '2023-05-10T09:33:00Z',
        status: 'delivered',
      },
    ],
    lastMessage: {
      id: 'msg-4',
      content:
        'No delays have been reported. The shipment is on schedule according to the Catena-X tracking system.',
      sender: 'system',
      timestamp: '2023-05-10T09:33:00Z',
      status: 'delivered',
    },
    timestamp: '2023-05-10T09:33:00Z',
  },
  {
    id: 'chat-2',
    name: 'Component Traceability',
    messages: [
      {
        id: 'msg-5',
        content: 'I need to trace the origin of component CX-COMP-789.',
        sender: 'user',
        timestamp: '2023-05-09T14:20:00Z',
        status: 'delivered',
      },
      {
        id: 'msg-6',
        content:
          'Component CX-COMP-789 was manufactured by Supplier XYZ on April 5th, 2023. It passed quality control on April 7th.',
        sender: 'system',
        timestamp: '2023-05-09T14:21:00Z',
        status: 'delivered',
      },
    ],
    lastMessage: {
      id: 'msg-6',
      content:
        'Component CX-COMP-789 was manufactured by Supplier XYZ on April 5th, 2023. It passed quality control on April 7th.',
      sender: 'system',
      timestamp: '2023-05-09T14:21:00Z',
      status: 'delivered',
    },
    timestamp: '2023-05-09T14:21:00Z',
  },
  {
    id: 'chat-3',
    name: 'Sustainability Metrics',
    messages: [
      {
        id: 'msg-7',
        content: 'What are the current CO2 emissions for our production line?',
        sender: 'user',
        timestamp: '2023-05-08T11:15:00Z',
        status: 'delivered',
      },
      {
        id: 'msg-8',
        content:
          'The current CO2 emissions for your production line are 45.3 tons for the last month, which is 12% lower than the previous month.',
        sender: 'system',
        timestamp: '2023-05-08T11:16:00Z',
        status: 'delivered',
      },
    ],
    lastMessage: {
      id: 'msg-8',
      content:
        'The current CO2 emissions for your production line are 45.3 tons for the last month, which is 12% lower than the previous month.',
      sender: 'system',
      timestamp: '2023-05-08T11:16:00Z',
      status: 'delivered',
    },
    timestamp: '2023-05-08T11:16:00Z',
  },
];
