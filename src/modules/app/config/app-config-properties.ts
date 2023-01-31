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
  configJson: `EDC_UI_CONFIG_JSON`,

  /**
   * Customer-Specific Feature Set and/or Theme.
   *
   * See {@link EDC_UI_PROFILE_DATA} in `edc-ui-profile.ts` for all available profiles.
   */
  activeProfile: 'EDC_UI_ACTIVE_PROFILE',

  /**
   * EDC Backend URL
   */
  dataManagementApiUrl: 'EDC_UI_DATA_MANAGEMENT_API_URL',

  /**
   * Hard-Coded API key (?)
   */
  dataManagementApiKey: 'EDC_UI_DATA_MANAGEMENT_API_KEY',

  /**
   * Pre-configured Connector IDs to be used in catalog browser, comma separated.
   */
  catalogUrls: 'EDC_UI_CATALOG_URLS',

  /**
   * Logout URL.
   */
  logoutUrl: 'EDC_UI_LOGOUT_URL',

  /**
   * Originator Connector URL.
   * When creating assets, this is added as asset property.
   */
  originator: 'EDC_UI_ASSET_PROP_ORIGINATOR',

  /**
   * Organization Name.
   * When creating assets, this is added as asset property.
   */
  originatorOrganization: 'EDC_UI_ASSET_PROP_ORIGINATOR_ORGANIZATION',
};
