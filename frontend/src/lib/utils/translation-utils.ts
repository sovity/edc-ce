/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

import {type TranslationKey} from '@/global';

export type TranslateFn = (key: TranslationKey) => string;

/**
 * Either use the "useTranslation" Hook or provide a fallback string
 */
export type TranslatedString = (t: TranslateFn) => string;

/**
 * Wrapper for faster instantiation of [TranslatedString]
 * @param key translation key
 */
export const byTranslation =
  (key: TranslationKey): TranslatedString =>
  (t) =>
    t(key) as any as string;

export const byFallback =
  (value: string): TranslatedString =>
  () =>
    value;
