import {EdcUiFeatureSet} from "./edc-ui-feature-set";

/**
 * Type-Safe Config as parsed from app-config.json
 */
export interface AppConfig {
  activeFeatureSet: EdcUiFeatureSet;
  dataManagementApiKey: string;
  dataManagementApiUrl: string;
  originator: string;
  catalogUrl: string;
  logoutUrl: string;
  originatorOrganization: string;
}
