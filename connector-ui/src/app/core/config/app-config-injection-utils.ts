/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {InjectionToken, Provider} from '@angular/core';
import {KeysOfType} from '../utils/type-utils';
import {APP_CONFIG, AppConfig} from './app-config';

/**
 * Provide individual {@link AppConfig} properties for better Angular Component APIs.
 *
 * @param token injection token
 * @param key property in {@link AppConfig}
 * @return {@link Provider}
 */
export const provideAppConfigProperty = <T>(
  token: InjectionToken<T>,
  key: KeysOfType<AppConfig, T>,
): Provider => ({
  provide: token,
  useFactory: (s: AppConfig) => s[key],
  deps: [APP_CONFIG],
});
