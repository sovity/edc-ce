/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import type en from '../messages/en.json';

declare type TranslationKey = keyof typeof en;

declare module 'next-intl' {
  interface AppConfig {
    Messages: typeof en;
  }
}
