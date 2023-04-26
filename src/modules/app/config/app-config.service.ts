import {Injectable} from '@angular/core';
import {EdcClient, buildEdcClient} from '@sovity.de/edc-client';
import {FaviconService} from '../../edc-demo/services/favicon.service';
import {AppConfig} from './app-config';

/**
 * Holds {@link AppConfig}
 */
@Injectable()
export class AppConfigService {
  // will be set by APP_INITIALIZER
  public config!: AppConfig;
  public edcClient!: EdcClient;

  constructor(private faviconService: FaviconService) {}

  setConfig(config: AppConfig) {
    console.log('Using AppConfig:', config);
    this.config = config;
    this.edcClient = buildEdcClient({
      managementApiUrl: config.dataManagementApiUrl,
      managementApiKey: config.dataManagementApiKey,
    });
    this.faviconService.setFavicon(config.brandFaviconSrc);
  }
}
