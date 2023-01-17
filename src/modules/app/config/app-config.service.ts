import {Injectable} from '@angular/core';
import {AppConfig} from './app-config';

/**
 * Holds {@link AppConfig}
 */
@Injectable()
export class AppConfigService {
  // will be set by APP_INITIALIZER
  public config!: AppConfig;
}
