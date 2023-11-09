import {InjectionToken} from '@angular/core';
import {EdcUiColorTheme} from './profiles/edc-ui-color-theme';
import {EdcUiFeature} from './profiles/edc-ui-feature';
import {EdcUiProfile} from './profiles/edc-ui-profile';
import {EdcUiRouteSet} from './profiles/edc-ui-route-set';

/**
 * Injection Token for {@link AppConfig}
 */
export const APP_CONFIG = new InjectionToken<AppConfig>('APP_CONFIG');

/**
 * Type-Safe and interpreted App Config
 *
 * See {@link AppConfigProperties} for available ENV Vars.
 */
export interface AppConfig {
  // selected profile
  profile: EdcUiProfile;
  features: Set<EdcUiFeature>;
  routes: EdcUiRouteSet;

  // selected theme (by profile)
  theme: EdcUiColorTheme;
  brandFaviconSrc: string;
  brandLogoSrc: string;
  brandLogoStyle: string;

  // EDC Backend Endpoints
  managementApiUrl: string;
  managementApiKey: string;
  logoutUrl: string; // requires feature flag logout-button

  // Other EDC Backend Endpoints
  catalogUrls: string;
  useFakeBackend: boolean;

  // Enterprise Edition
  showEeBasicMarketing: boolean;
}
