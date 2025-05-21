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
import {ConnectionStatusDto} from '@/lib/api/models/connection-status-dto';

export const getStatusColor = (status: ConnectionStatusDto) => {
  switch (status) {
    case ConnectionStatusDto.ONLINE:
      return 'text-green-500';
    case ConnectionStatusDto.CONNECTING:
      return 'text-amber-500';
    case ConnectionStatusDto.ERROR:
      return 'text-red-500';
    default:
      return 'text-gray-500';
  }
};
