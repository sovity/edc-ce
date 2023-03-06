import {MDS_THEME, SOVITY_THEME} from './edc-ui-theme-data';
import {MANAGED_EDC_FEATURES} from './feature-sets/managed-edc-features';
import {MDS_FEATURES} from './feature-sets/mds-features';
import {OPEN_SOURCE_EDC_FEATURES} from './feature-sets/open-source-edc-features';
import {inferEdcUiProfileType} from './infer-edc-ui-profile-type';

/**
 * List of available profiles.
 *
 * This codebase supports multiple profiles since it incorporates multiple deployment targets.
 */
export const EDC_UI_PROFILE_DATA = inferEdcUiProfileType({
  'sovity-open-source': {
    ...SOVITY_THEME,
    features: new Set(OPEN_SOURCE_EDC_FEATURES),
  },
  'sovity-hosted-by-sovity': {
    ...SOVITY_THEME,
    features: new Set(MANAGED_EDC_FEATURES),
  },
  'mds-open-source': {
    ...MDS_THEME,
    features: new Set([...MDS_FEATURES, ...OPEN_SOURCE_EDC_FEATURES]),
  },
  'mds-hosted-by-sovity': {
    ...MDS_THEME,
    features: new Set([...MDS_FEATURES, ...MANAGED_EDC_FEATURES]),
  },
});
