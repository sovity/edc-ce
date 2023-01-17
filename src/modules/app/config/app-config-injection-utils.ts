import {InjectionToken, Provider} from '@angular/core';
import {KeysOfType} from '../../edc-demo/utils/type-utils';
import {AppConfig} from './app-config';
import {AppConfigService} from './app-config.service';

/**
 * Provide individual {@link AppConfig} properties for better Angular Component APIs.
 *
 * @param token injection token
 * @param key property in {@link AppConfig}
 * @return {@link Provider}
 */
export const provideConfigProperty = <T>(
  token: InjectionToken<T>,
  key: KeysOfType<AppConfig, T>,
): Provider => ({
  provide: token,
  useFactory: (s: AppConfigService) => s.config[key],
  deps: [AppConfigService],
});
