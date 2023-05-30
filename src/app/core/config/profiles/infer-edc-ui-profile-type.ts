import {EdcUiProfileConfig} from './edc-ui-profile-config';

/**
 * Type utility for inferring the keys of EDC_UI_PROFILE_DATA as type.
 * see https://stackoverflow.com/a/74691877
 *
 * @param profiles Record<EdcUiProfile, EdcUiProfileUtils>
 */
export const inferEdcUiProfileType = <
  T extends Record<string, EdcUiProfileConfig>,
>(
  profiles: T,
) => profiles;
