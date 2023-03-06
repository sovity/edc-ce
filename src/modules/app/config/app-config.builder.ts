import {Injectable} from '@angular/core';
import {AppConfig} from './app-config';
import {AppConfigProperties} from './app-config-properties';
import {getProfileOrFallback} from './profiles/get-profile-or-fallback';

@Injectable()
export class AppConfigBuilder {
  /**
   * Build {@link AppConfig} from ENV Vars
   *
   * @param vars env vars
   */
  buildAppConfig(vars: Record<string, string | null>): AppConfig {

    const {profile, profileConfig} = getProfileOrFallback(
      vars[AppConfigProperties.activeProfile],
    );

    return {
      // profile and theme
      profile,
      ...profileConfig,

      // EDC Backend Endpoints
      connectorEndpoint:
        vars[AppConfigProperties.connectorEndpoint] ??
        vars[AppConfigProperties._legacyConnectorEndpoint] ??
        'https://no-connector-endpoint-configured',
      dataManagementApiKey:
        vars[AppConfigProperties.dataManagementApiKey] ??
        'no-api-key-configured',
      dataManagementApiUrl:
        vars[AppConfigProperties.dataManagementApiUrl] ??
        'https://no-backend-api-url-configured',
      logoutUrl:
        vars[AppConfigProperties.logoutUrl] ??
        'https://no-logout-url-configured',

      // Other EDC Backend Endpoints
      catalogUrls: vars[AppConfigProperties.catalogUrls] ?? '',

      // Connector Self-Description
      connectorId:
        vars[AppConfigProperties.connectorId] ??
        'https://missing-edc-connector-id',
      connectorName:
        vars[AppConfigProperties.connectorName] ??
        'EDC Connector (No Name Configured)',
      connectorIdsId:
        vars[AppConfigProperties.connectorIdsId] ??
        'no-ids-connector-id-configured',
      connectorIdsTitle:
        vars[AppConfigProperties.connectorIdsTitle] ??
        'EDC Connector (No Title Configured)',
      connectorIdsDescription:
        vars[AppConfigProperties.connectorIdsDescription] ??
        'No Connector Description was configured.',
      curatorUrl:
        vars[AppConfigProperties.curatorUrl] ??
        'http://no-curator-url-configured',
      curatorOrganizationName:
        vars[AppConfigProperties.curatorOrganizationName] ??
        vars[AppConfigProperties._legacyCuratorOrganizationName] ??
        'No Curator Organization Name Configured',
      dapsOauthTokenUrl:
        vars[AppConfigProperties.dapsOauthTokenUrl] ??
        'http://no-daps-oauth-token-url-configured',
      dapsOauthJwksUrl:
        vars[AppConfigProperties.dapsOauthJwksUrl] ??
        'http://no-daps-oauth-jwks-url-configured',
      maintainerUrl:
        vars[AppConfigProperties.maintainerUrl] ??
        'http://no-maintainer-url-configured',
      maintainerOrganizationName:
        vars[AppConfigProperties.maintainerOrganizationName] ??
        'No Maintainer Organization Name Configured',
    };
  }
}
