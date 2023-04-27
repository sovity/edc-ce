import {EdcUiColorTheme} from './profiles/edc-ui-color-theme';
import {EdcUiFeature} from './profiles/edc-ui-feature';
import {EdcUiProfile} from './profiles/edc-ui-profile';

/**
 * Type-Safe and interpreted App Config
 *
 * See {@link AppConfigProperties} for available ENV Vars.
 */
export interface AppConfig {
  // selected profile
  profile: EdcUiProfile;
  features: Set<EdcUiFeature>;

  // selected theme (by profile)
  theme: EdcUiColorTheme;
  brandFaviconSrc: string;
  brandLogoSrc: string;
  brandLogoStyle: string;

  // EDC Backend Endpoints
  connectorEndpoint: string;
  managementApiUrl: string;
  managementApiKey: string;
  logoutUrl: string; // requires feature flag logout-button

  // Other EDC Backend Endpoints
  catalogUrls: string;

  // Connector Self-Description
  connectorId: string;
  connectorName: string;
  connectorIdsId: string;
  connectorIdsTitle: string;
  connectorIdsDescription: string;
  curatorUrl: string;
  curatorOrganizationName: string;
  dapsOauthTokenUrl: string;
  dapsOauthJwksUrl: string;
  maintainerUrl: string;
  maintainerOrganizationName: string;
}
