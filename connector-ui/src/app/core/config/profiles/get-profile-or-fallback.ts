import {AppConfigProperties} from '../app-config-properties';
import {EdcUiProfile} from './edc-ui-profile';
import {EdcUiProfileConfig} from './edc-ui-profile-config';
import {EDC_UI_PROFILE_DATA} from './edc-ui-profile-data';

/**
 * Find profile (or default to first)
 * @param profile profile
 */
export function getProfileOrFallback(profile?: string | null): {
  profile: EdcUiProfile;
  profileConfig: EdcUiProfileConfig;
} {
  if (EDC_UI_PROFILE_DATA[profile as EdcUiProfile]) {
    return {
      profile: profile as EdcUiProfile,
      profileConfig: EDC_UI_PROFILE_DATA[profile as EdcUiProfile],
    };
  }

  const fallback: EdcUiProfile = 'sovity-open-source';

  const availableProfiles = Object.keys(EDC_UI_PROFILE_DATA)
    .map((s) => `"${s}"`)
    .join(', ');

  console.error(
    `Invalid ${AppConfigProperties.activeProfile}: ${JSON.stringify(profile)}.`,
    `Expected one of ${availableProfiles}.`,
    `Falling back to ${JSON.stringify(fallback)}'.`,
  );

  return {
    profile: fallback,
    profileConfig: EDC_UI_PROFILE_DATA[fallback],
  };
}
