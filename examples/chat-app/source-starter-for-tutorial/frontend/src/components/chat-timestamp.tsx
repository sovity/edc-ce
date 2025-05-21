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
import {format} from 'date-fns';

interface EnhancedTimeAgoProps {
  date: string | number | Date;
}

export const ChatTimestamp = ({date}: EnhancedTimeAgoProps) => {
  const dateObj = date instanceof Date ? date : new Date(date);
  const now = new Date();

  if (isNaN(dateObj.getTime())) {
    console.error('Invalid message timestamp:', date);
    return '';
  }

  const isSameDay =
    now.getMonth() === dateObj.getMonth() && now.getDay() === dateObj.getDay();

  return isSameDay
    ? format(dateObj, 'HH:mm')
    : format(dateObj, 'yyyy-MM-dd HH:mm');
};
