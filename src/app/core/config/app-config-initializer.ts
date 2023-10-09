import {Provider} from '@angular/core';
import {APP_CONFIG, AppConfig} from './app-config';
import {AppConfigBuilder} from './app-config.builder';
import {AppConfigFetcher} from './app-config.fetcher';
import {AppConfigMerger} from './app-config.merger';

let appConfig: AppConfig | null = null;

export async function loadAppConfig(): Promise<AppConfig> {
  const merger = new AppConfigMerger();
  const builder = new AppConfigBuilder();
  const fetcher = new AppConfigFetcher(merger);
  return fetcher
    .fetchEffectiveConfig('/assets/config/app-config.json', null)
    .then((json) => builder.buildAppConfig(json))
    .then((config) => (appConfig = config));
}

export const provideAppConfig = (): Provider => ({
  provide: APP_CONFIG,
  useFactory: () => appConfig,
});
