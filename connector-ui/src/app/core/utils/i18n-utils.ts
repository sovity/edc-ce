/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
export interface AvailableLanguage {
  code: string;
  name: string;
}

export const supportedLanguages: AvailableLanguage[] = [
  {code: 'en', name: 'English'},
  {code: 'de', name: 'Deutsch'},
];

export const isLanguageSupported = (value: unknown): value is string =>
  supportedLanguages.map((it) => it.code).includes(value as string);
