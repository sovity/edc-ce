import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {firstValueFrom} from 'rxjs';
import urlJoin from 'url-join';
import {validUrlPattern} from '../../edc-demo/validators/url-validator';
import {AppConfigProperties} from './app-config-properties';
import {AppConfigBuilder} from './app-config.builder';
import {AppConfigMerger} from './app-config.merger';

@Injectable()
export class AppConfigFetcher {
  constructor(
    private http: HttpClient,
    private appConfigMerger: AppConfigMerger,
    private appConfigBuilder: AppConfigBuilder,
  ) {}

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
      apiKey = this.appConfigBuilder.getManagementApiKey(config) ?? apiKey;
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
    return firstValueFrom(
      this.http.get<Record<string, string | null>>(path, {headers}),
    ).catch((err) => {
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
    const dataManagementApiUrl =
      this.appConfigBuilder.getManagementApiUrl(config);

    if (!dataManagementApiUrl) {
      console.error(
        `Invalid value for ${AppConfigProperties.configUrl} and ${AppConfigProperties.managementApiUrl}. Could not build Additional Config URL:`,
        relativeUrl,
        dataManagementApiUrl,
      );
      return null;
    }

    return urlJoin(dataManagementApiUrl!!, relativeUrl);
  }
}
