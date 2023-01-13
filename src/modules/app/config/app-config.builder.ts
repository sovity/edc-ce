import {Injectable} from '@angular/core';
import {AppConfig} from "./app-config";
import {AppConfigProperties} from "./app-config-properties";
import {ALL_EDC_UI_FEATURE_SETS, EdcUiFeatureSet} from "./edc-ui-feature-set";

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
      ...envVars
    }

    const activeFeatureSet: EdcUiFeatureSet = this.findOrDefaultToFirst(
      ALL_EDC_UI_FEATURE_SETS,
      vars[AppConfigProperties.activeFeatureSet]
    );

    return {
      activeFeatureSet,
      dataManagementApiKey: vars[AppConfigProperties.dataManagementApiKey] ?? 'no-api-key-configured',
      dataManagementApiUrl: vars[AppConfigProperties.dataManagementApiUrl] ?? 'https://no-backend-api-url-configured',
      originator: vars[AppConfigProperties.originator] ?? 'https://no-originator-configured',
      originatorOrganization: vars[AppConfigProperties.originatorOrganization] ?? 'No Originator Organization Configured',
      catalogUrl: vars[AppConfigProperties.catalogUrls] ?? '',
      logoutUrl: vars[AppConfigProperties.logoutUrl] ?? 'https://no-logout-url-configured',
    }
  }

  private findOrDefaultToFirst<T extends string>(availableValues: T[], value: string): T {
    return availableValues.includes(value as T) ? value as T : availableValues[0];
  }
}
