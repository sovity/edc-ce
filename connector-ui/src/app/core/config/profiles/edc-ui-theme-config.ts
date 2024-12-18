import {AppConfig} from '../app-config';

/**
 * Type-Safe and interpreted App Config
 */
export type EdcUiThemeConfig = Pick<
  AppConfig,
  'theme' | 'brandLogoStyle' | 'brandLogoSrc' | 'brandFaviconSrc'
>;
