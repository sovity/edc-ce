import {AppConfigProperties} from './app-config-properties';
import {inferEdcUiProfileDataKeyTypes} from './edc-ui-profile-config';

/**
 * List of available profiles.
 *
 * This codebase supports multiple profiles since it incorporates multiple deployment targets.
 */
export const EDC_UI_PROFILE_DATA = inferEdcUiProfileDataKeyTypes({
  'sovity-open-source': {
    theme: 'theme-sovity',
    brandLogoSrc: '/assets/images/sovity_logo.svg',
    features: new Set(),
  },
  'sovity-hosted-by-sovity': {
    theme: 'theme-sovity',
    brandLogoSrc: '/assets/images/sovity_logo.svg',
    features: new Set(['sovity-zammad-integration', 'logout-button']),
  },
  'mds-open-source': {
    theme: 'theme-mds',
    brandLogoSrc: '/assets/images/mds_logo.svg',
    features: new Set(['mds-fields']),
  },
  'mds-hosted-by-sovity': {
    theme: 'theme-mds',
    brandLogoSrc: '/assets/images/mds_logo.svg',
    features: new Set([
      'mds-fields',
      'sovity-zammad-integration',
      'logout-button',
    ]),
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
