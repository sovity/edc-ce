/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
/**
 * Supported Config ENV Vars
 *
 * All ENV Vars need to start with EDC_UI_ because only those will be written into app-configuration.json.
 *
 * NB! Keep in sync!
 *
 * docs/deployment-guide/config/connector-ui/README.md
 */
export const AppConfigProperties = {
  configJson: 'EDC_UI_CONFIG_JSON',
  configUrl: 'EDC_UI_CONFIG_URL',
  activeProfile: 'EDC_UI_ACTIVE_PROFILE',
  managementApiUrl: 'EDC_UI_MANAGEMENT_API_URL',
  managementApiKey: 'EDC_UI_MANAGEMENT_API_KEY',
  shownManagementApiUrl: 'EDC_UI_MANAGEMENT_API_URL_SHOWN_IN_DASHBOARD',
  logoutUrl: 'EDC_UI_LOGOUT_URL',
  catalogUrls: 'EDC_UI_CATALOG_URLS',
  useFakeBackend: 'EDC_UI_USE_FAKE_BACKEND',
  showEeBasicMarketing: 'EDC_UI_SHOW_EE_BASIC_MARKETING',
  buildDate: 'EDC_UI_BUILD_DATE',
  buildVersion: 'EDC_UI_BUILD_VERSION',
};
