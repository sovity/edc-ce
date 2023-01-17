import {Injectable} from '@angular/core';
import {AppConfig} from './app-config';
import {AppConfigProperties} from './app-config-properties';
import {EDC_UI_PROFILE_DATA, getProfileOrFallback} from './edc-ui-profile';

@Injectable()
export class AppConfigBuilder {
  /**
   * Build {@link AppConfig} from ENV Vars
   *
   * @param envVars env vars
   */
  buildAppConfig(envVars: Record<string, string | null>): AppConfig {
    const vars = {
      ...JSON.parse(envVars[AppConfigProperties.configJson] || '{}'),
      ...envVars,
    };

    const profile = getProfileOrFallback(
      vars[AppConfigProperties.activeProfile],
    );

    return {
      profile,
      ...EDC_UI_PROFILE_DATA[profile],
      dataManagementApiKey:
        vars[AppConfigProperties.dataManagementApiKey] ??
        'no-api-key-configured',
      dataManagementApiUrl:
        vars[AppConfigProperties.dataManagementApiUrl] ??
        'https://no-backend-api-url-configured',
      originator:
        vars[AppConfigProperties.originator] ??
        'https://no-originator-configured',
      originatorOrganization:
        vars[AppConfigProperties.originatorOrganization] ??
        'No Originator Organization Configured',
      catalogUrl: vars[AppConfigProperties.catalogUrls] ?? '',
      logoutUrl:
        vars[AppConfigProperties.logoutUrl] ??
        'https://no-logout-url-configured',
    };
  }
}
