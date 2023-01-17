import {AppConfig} from "./app-config";
import {Injectable} from "@angular/core";

/**
 * Holds {@link AppConfig}
 */
@Injectable()
export class AppConfigService {
  // will be set by APP_INITIALIZER
  public config!: AppConfig;
}
