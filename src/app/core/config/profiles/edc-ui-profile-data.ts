import {MDS_THEME, SOVITY_THEME} from './edc-ui-theme-data';
import {COMMUNITY_EDITION_FEATURES} from './feature-sets/community-edition-features';
import {ENTERPRISE_EDITION_FEATURES} from './feature-sets/enterprise-edition-features';
import {MDS_FEATURES} from './feature-sets/mds-features';
import {inferEdcUiProfileType} from './infer-edc-ui-profile-type';

/**
 * List of available profiles.
 *
 * This codebase supports multiple profiles since it incorporates multiple deployment targets.
 */
export const EDC_UI_PROFILE_DATA = inferEdcUiProfileType({
  'sovity-open-source': {
    ...SOVITY_THEME,
    routes: 'connector-ui',
    features: new Set(COMMUNITY_EDITION_FEATURES),
  },
  'sovity-hosted-by-sovity': {
    ...SOVITY_THEME,
    routes: 'connector-ui',
    features: new Set(ENTERPRISE_EDITION_FEATURES),
  },
  'mds-open-source': {
    ...MDS_THEME,
    routes: 'connector-ui',
    features: new Set([...MDS_FEATURES, ...COMMUNITY_EDITION_FEATURES]),
  },
  'mds-hosted-by-sovity': {
    ...MDS_THEME,
    routes: 'connector-ui',
    features: new Set([
      'mds-marketing',
      ...MDS_FEATURES,
      ...ENTERPRISE_EDITION_FEATURES,
    ]),
  },
  'mds-blue-hosted-by-sovity': {
    ...SOVITY_THEME,
    routes: 'connector-ui',
    features: new Set([...MDS_FEATURES, ...ENTERPRISE_EDITION_FEATURES]),
  },
  broker: {
    ...MDS_THEME,
    routes: 'broker-ui',
    features: new Set(MDS_FEATURES),
  },
});
