import {AppConfig} from '../app-config';
import {EdcUiThemeConfig} from './edc-ui-theme-config';

export type EdcUiProfileConfig = Pick<AppConfig, 'features' | 'routes'> &
  EdcUiThemeConfig;
