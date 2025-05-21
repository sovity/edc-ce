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
import {CounterpartyDto} from '@/lib/api/models/counterparty-dto';
import {ConnectionStatusDto} from '@/lib/api/models/connection-status-dto';
import {CounterpartyAddDto} from '@/lib/api/models/counterparty-add-dto';

export const fakeCounterparties: CounterpartyDto[] = [
  {
    participantId: 'BPNL00000001',
    connectorEndpoint: 'https//connector1.example.com/control/dsp',
    status: ConnectionStatusDto.ONLINE,
    lastUpdate: new Date(),
  },
  {
    participantId: 'BPNL00000002',
    connectorEndpoint: 'https//connector2.example.com/control/dsp',
    status: ConnectionStatusDto.ERROR,
    lastUpdate: new Date(),
  },
];

export const addCounterparty = (toAdd: CounterpartyAddDto): CounterpartyDto => {
  const created: CounterpartyDto = {
    participantId: toAdd.participantId,
    connectorEndpoint: toAdd.connectorEndpoint,
    status: ConnectionStatusDto.CONNECTING,
    lastUpdate: new Date(),
  };

  const index = fakeCounterparties.push(created) - 1;

  setTimeout(() => {
    fakeCounterparties[index].status = ConnectionStatusDto.ONLINE;
    fakeCounterparties[index].lastUpdate = new Date();
  }, 5000);

  return created;
};

export const deleteCounterparty = (participantId: string): boolean => {
  const index = fakeCounterparties.findIndex(
    (it) => it.participantId === participantId,
  );
  if (index >= 0) {
    fakeCounterparties.splice(index, 1);
    return true;
  } else {
    return false;
  }
};
