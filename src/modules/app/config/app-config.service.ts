import {Injectable} from '@angular/core';
import {AppConfig} from './app-config';

/**
 * Holds {@link AppConfig}
 */
@Injectable()
export class AppConfigService {
  // will be set by APP_INITIALIZER
  public config!: AppConfig;

  setConfig(config: AppConfig) {
    this.config = config;
    console.log('Using AppConfig:', config);
  }
}
