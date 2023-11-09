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
   * If this URL is relative, it will be appended to the {@link AppConfigProperties.managementApiUrl}.
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
   * Pre-configured Other Connector Endpoints to be used in catalog browser, comma separated.
   */
  catalogUrls: 'EDC_UI_CATALOG_URLS',

  /**
   * Whether to use the fake backend (local development).
   */
  useFakeBackend: 'EDC_UI_USE_FAKE_BACKEND',

  /**
   * Only for Enterprise Edition.
   * Enables Marketing for other Enterprise Edition Variants.
   */
  showEeBasicMarketing: 'EDC_UI_SHOW_EE_BASIC_MARKETING',
};
