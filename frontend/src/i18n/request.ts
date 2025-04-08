/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {getRequestConfig} from 'next-intl/server';
import {unflattenRecord} from '@/lib/utils/record-utils';

export default getRequestConfig(async () => {
  // Provide a static locale, fetch a user setting,
  // read from `cookies()`, `headers()`, etc.
  const locale = 'en';

  // Read the right locale's messages
  // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
  const messages: Record<string, string> =
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    (await import(`../../messages/${locale}.json`)).default;

  // Unflatten the structure for next intl
  const messagesFlat = unflattenRecord(messages, '.');

  // Return the locale and messages
  return {
    locale,
    messages: messagesFlat,
  };
});
