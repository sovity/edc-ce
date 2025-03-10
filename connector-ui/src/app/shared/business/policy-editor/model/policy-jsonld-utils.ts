/*
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
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
 *     Fraunhofer FIT - contributed initial internationalization support
 *     sovity - continued development
 */
import {UiPolicyLiteral} from '@sovity.de/edc-client';
import {filterNonNull} from '../../../../core/utils/array-utils';

export const readSingleStringLiteral = (
  literal: UiPolicyLiteral,
): string | null => {
  if (literal.type === 'STRING') {
    return literal.value ?? null;
  } else if (literal.type === 'STRING_LIST') {
    return literal.valueList?.length ? literal.valueList[0] : null;
  }
  return null;
};

export const readArrayLiteral = (literal: UiPolicyLiteral): string[] => {
  if (literal.type === 'STRING') {
    return filterNonNull([literal.value]);
  } else if (literal.type === 'STRING_LIST') {
    return literal.valueList ?? [];
  }
  return [];
};

export const readJsonLiteral = (literal: UiPolicyLiteral): string => {
  if (literal.type === 'STRING') {
    return JSON.stringify(literal.value);
  } else if (literal.type === 'STRING_LIST') {
    return JSON.stringify(literal.valueList);
  }
  return literal.value ?? 'null';
};

export const stringLiteral = (
  value: string | null | undefined,
): UiPolicyLiteral => ({
  type: 'STRING',
  value: value ?? undefined,
});
