import urlJoin from 'url-join';
import {validUrlPattern} from '../validators/url-validator';
import {AppConfigProperties} from './app-config-properties';
import {AppConfigMerger} from './app-config.merger';

export class AppConfigFetcher {
  constructor(private appConfigMerger: AppConfigMerger) {}

  /**
   * Fetches app-config.json, applies {@link AppConfigProperties.configJson},
   * fetches another config from {@link AppConfigProperties.configUrl}, and
   * merges the results.
   */
  async fetchEffectiveConfig(
    configUrl: string,
    apiKey: string | null,
  ): Promise<Record<string, string | null>> {
    let config = await this.fetchConfigJson(configUrl, apiKey);
    config = this.appConfigMerger.applyEmbeddedConfig(configUrl, config);

    const additionalConfigUrl = this.buildAdditionConfigUrl(config);
    if (additionalConfigUrl) {
      apiKey = config[AppConfigProperties.managementApiKey] ?? apiKey;
      const additionalConfig = await this.fetchEffectiveConfig(
        additionalConfigUrl,
        apiKey,
      );
      config = this.appConfigMerger.mergeConfigs(
        configUrl,
        config,
        additionalConfigUrl,
        additionalConfig,
      );
    }

    return config;
  }

  private fetchConfigJson(
    path: string,
    apiKey: string | null,
  ): Promise<Record<string, string | null>> {
    const headers = apiKey ? {'X-API-KEY': apiKey} : undefined;

    // We fetch the config using the Fetch API because we want to fetch it before application initialization
    // At this time the Angular Http Client is not ready yet
    return fetch(path, {headers})
      .then((response) => response.json())
      .catch((err) => {
        console.error(`Could not fetch app-config.json from ${path}`, err);
        return {};
      });
  }

  private buildAdditionConfigUrl(
    config: Record<string, string | null>,
  ): string | null {
    const relativeUrl = config[AppConfigProperties.configUrl];
    if (!relativeUrl) {
      return null;
    }

    // Absolute URL
    if (validUrlPattern.test(relativeUrl)) {
      return relativeUrl;
    }

    // Relative URL
    const managementApiUrl = config[AppConfigProperties.managementApiUrl];

    if (!managementApiUrl) {
      console.error(
        `Invalid value for ${AppConfigProperties.configUrl} and ${AppConfigProperties.managementApiUrl}. Could not build Additional Config URL:`,
        relativeUrl,
        managementApiUrl,
      );
      return null;
    }

    return urlJoin(managementApiUrl, relativeUrl);
  }
}
