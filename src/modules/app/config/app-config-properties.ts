/**
 * Supported Config ENV Vars
 *
 * All ENV Vars need to start with EDC_UI_ because only those will be written into app-config.json.
 */
export const AppConfigProperties = {
  /**
   * Instead of providing multiple ENV Vars,
   * provide a single one as JSON.
   *
   * Individual ENV Vars will take precedence over this JSON.
   */
  configJson: 'EDC_UI_CONFIG_JSON',

  /**
   * Additional URL to fetch a Config JSON from that will take precedence.
   *
   * This allows an EDC Backend Extension to provide EDC UI configuration
   *
   * If this URL is relative, it will be appended to the {@link AppConfigProperties.dataManagementApiUrl}.
   *
   * The JSON should be a {@link Record<string, string>}
   */
  configUrl: 'EDC_UI_CONFIG_URL',

  /**
   * Customer-Specific Feature Set and/or Theme.
   *
   * See {@link EDC_UI_PROFILE_DATA} for all available profiles.
   */
  activeProfile: 'EDC_UI_ACTIVE_PROFILE',

  /**
   * EDC Backend URL
   */
  managementApiUrl: 'EDC_UI_MANAGEMENT_API_URL',

  /**
   * Hard-Coded API key (?)
   */
  managementApiKey: 'EDC_UI_MANAGEMENT_API_KEY',

  /**
   * Logout URL.
   */
  logoutUrl: 'EDC_UI_LOGOUT_URL',

  /**
   * Current Connector Endpoint.
   * When creating assets, this is added as asset property.
   */
  connectorEndpoint: 'EDC_UI_CONNECTOR_ENDPOINT',

  /**
   * Pre-configured Other Connector Endpoints to be used in catalog browser, comma separated.
   */
  catalogUrls: 'EDC_UI_CATALOG_URLS',

  /**
   * Connector ID, usually https://hostname of EDC backend.
   *
   * E.g. contained in DAT Claim "referringConnector".
   * And will be used to realize the connector restricted policy.
   */
  connectorId: 'EDC_UI_CONNECTOR_ID',

  /**
   * Connector Name, e.g. used by broker extension
   */
  connectorName: 'EDC_UI_CONNECTOR_NAME',

  /**
   * Connector IDS ID, e.g. urn:connector:sth
   */
  connectorIdsId: 'EDC_UI_IDS_ID',

  /**
   * Connector Title, e.g. "Sovity Test Connector 001"
   */
  connectorIdsTitle: 'EDC_UI_IDS_TITLE',

  /**
   * Connector Description Text
   */
  connectorIdsDescription: 'EDC_UI_IDS_DESCRIPTION',

  /**
   * DAPS OAUTH Token URL
   */
  dapsOauthTokenUrl: 'EDC_UI_DAPS_OAUTH_TOKEN_URL',

  /**
   * DAPS OAUTH JWKS URL
   */
  dapsOauthJwksUrl: 'EDC_UI_DAPS_OAUTH_JWKS_URL',

  /**
   * Curator Organization Homepage
   */
  curatorUrl: 'EDC_UI_CURATOR_URL',

  /**
   * Curator Organization Name.
   * When creating assets, this is added as asset property.
   */
  curatorOrganizationName: 'EDC_UI_CURATOR_ORGANIZATION_NAME',

  /**
   * Maintainer Organization Homepage
   */
  maintainerUrl: 'EDC_UI_MAINTAINER_URL',

  /**
   * Maintainer Organization Name.
   */
  maintainerOrganizationName: 'EDC_UI_MAINTAINER_ORGANIZATION_NAME',

  /**
   * Same as {@link connectorEndpoint}.
   *
   * @deprecated Use {@link connectorEndpoint} instead.
   */
  _legacyConnectorEndpoint: 'EDC_UI_ASSET_PROP_ORIGINATOR',

  /**
   * Same as {@link curatorOrganizationName}.
   *
   * @deprecated Use {@link curatorOrganizationName} instead.
   */
  _legacyCuratorOrganizationName: 'EDC_UI_ASSET_PROP_ORIGINATOR_ORGANIZATION',

  /**
   * Same as {@link managementApiUrl}.
   *
   * @deprecated Use {@link managementApiUrl} instead.
   */
  _legacyManagementApiUrl: 'EDC_UI_DATA_MANAGEMENT_API_URL',

  /**
   * Same as {@link managementApiKey}.
   *
   * @deprecated Use {@link managementApiKey} instead.
   */
  _legacyManagementApiKey: 'EDC_UI_DATA_MANAGEMENT_API_KEY',
};
