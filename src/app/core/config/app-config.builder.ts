import {Injectable} from '@angular/core';
import {AppConfig} from './app-config';
import {AppConfigProperties} from './app-config-properties';
import {getProfileOrFallback} from './profiles/get-profile-or-fallback';

@Injectable()
export class AppConfigBuilder {
  /**
   * Build {@link AppConfig} from ENV Vars
   *
   * @param vars env vars
   */
  buildAppConfig(vars: Record<string, string | null>): AppConfig {
    const {profile, profileConfig} = getProfileOrFallback(
      vars[AppConfigProperties.activeProfile],
    );

    return {
      // profile and theme
      profile,
      ...profileConfig,

      // EDC Backend Endpoints
      managementApiKey:
        vars[AppConfigProperties.managementApiKey] ?? 'no-api-key-configured',
      managementApiUrl:
        vars[AppConfigProperties.managementApiUrl] ??
        'https://no-backend-api-url-configured',
      logoutUrl:
        vars[AppConfigProperties.logoutUrl] ??
        'https://no-logout-url-configured',

      // Other EDC Backend Endpoints
      catalogUrls: vars[AppConfigProperties.catalogUrls] ?? '',
      useFakeBackend: vars[AppConfigProperties.useFakeBackend] === 'true',

      // Enterprise Edition
      showEeBasicMarketing:
        vars[AppConfigProperties.showEeBasicMarketing] === 'true',
    };
  }
}
