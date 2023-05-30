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
