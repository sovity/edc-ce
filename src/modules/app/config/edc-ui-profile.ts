import {AppConfigProperties} from './app-config-properties';
import {EdcUiFeature} from './edc-ui-feature';
import {
  EdcUiProfileConfig,
  inferEdcUiProfileDataKeyTypes,
} from './edc-ui-profile-config';

type EdcUiTheme = Pick<
  EdcUiProfileConfig,
  'theme' | 'brandLogoSrc' | 'brandLogoStyle'
>;

const SOVITY_THEME: EdcUiTheme = {
  theme: 'theme-sovity',
  brandLogoSrc: '/assets/images/sovity_logo.svg',
  brandLogoStyle: 'width: 70%;',
};

const MDS_THEME: EdcUiTheme = {
  theme: 'theme-mds',
  brandLogoSrc: '/assets/images/mds_logo.svg',
  brandLogoStyle: 'height: 57px; margin-top: 5px; margin-left: 5px;',
};

const MDS_FEATURES: EdcUiFeature[] = ['mds-fields'];

const OPEN_SOURCE_EDC_FEATURES: EdcUiFeature[] = ['open-source-marketing'];

const MANAGED_EDC_FEATURES: EdcUiFeature[] = [
  'sovity-zammad-integration',
  'logout-button',
];

/**
 * List of available profiles.
 *
 * This codebase supports multiple profiles since it incorporates multiple deployment targets.
 */
export const EDC_UI_PROFILE_DATA = inferEdcUiProfileDataKeyTypes({
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

/**
 * Available Configuration Profiles.
 */
export type EdcUiProfile = keyof typeof EDC_UI_PROFILE_DATA;

/**
 * Find profile (or default to first)
 * @param profile profile
 */
export function getProfileOrFallback(profile?: string | null): EdcUiProfile {
  if (EDC_UI_PROFILE_DATA[profile as EdcUiProfile]) {
    return profile as EdcUiProfile;
  }

  const fallback: EdcUiProfile = 'sovity-open-source';

  let availableProfiles = Object.keys(EDC_UI_PROFILE_DATA)
    .map((s) => `"${s}"`)
    .join(', ');

  console.error(
    `Invalid ${AppConfigProperties.activeProfile}: ${JSON.stringify(profile)}.`,
    `Expected one of ${availableProfiles}.`,
    `Falling back to ${JSON.stringify(fallback)}'.`,
  );

  return fallback;
}
