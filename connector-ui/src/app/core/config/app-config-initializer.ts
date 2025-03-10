/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {StaticProvider} from '@angular/core';
import {APP_CONFIG, AppConfig} from './app-config';
import {AppConfigBuilder} from './app-config.builder';
import {AppConfigFetcher} from './app-config.fetcher';
import {AppConfigMerger} from './app-config.merger';

export async function loadAppConfig(): Promise<AppConfig> {
  const merger = new AppConfigMerger();
  const builder = new AppConfigBuilder();
  const fetcher = new AppConfigFetcher(merger);
  return fetcher
    .fetchEffectiveConfig('/assets/config/app-configuration.json', null)
    .then((json) => builder.buildAppConfig(json));
}

export const provideAppConfig = (appConfig: AppConfig): StaticProvider => ({
  provide: APP_CONFIG,
  useValue: appConfig,
});
