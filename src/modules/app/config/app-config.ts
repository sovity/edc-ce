import {EdcUiColorTheme} from './edc-ui-color-theme';
import {EdcUiFeature} from './edc-ui-feature';
import {EdcUiProfile} from './edc-ui-profile';

/**
 * Type-Safe and interpreted App Config
 */
export type AppConfig = {
  profile: EdcUiProfile;
  theme: EdcUiColorTheme;
  brandLogoSrc: string;
  features: Set<EdcUiFeature>;
  dataManagementApiKey: string;
  dataManagementApiUrl: string;
  originator: string;
  catalogUrl: string;
  logoutUrl: string;
  originatorOrganization: string;
};
