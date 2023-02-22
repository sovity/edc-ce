import {Injectable} from '@angular/core';
import {FaviconService} from '../../edc-demo/services/favicon.service';
import {AppConfig} from './app-config';

/**
 * Holds {@link AppConfig}
 */
@Injectable()
export class AppConfigService {
  // will be set by APP_INITIALIZER
  public config!: AppConfig;

  constructor(private faviconService: FaviconService) {}

  setConfig(config: AppConfig) {
    console.log('Using AppConfig:', config);
    this.config = config;
    this.faviconService.setFavicon(config.brandFaviconSrc);
  }
}
