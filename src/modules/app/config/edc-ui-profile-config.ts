import {AppConfig} from './app-config';

/**
 * Parts of AppConfig decided by profile / feature set.
 */
export type EdcUiProfileConfig = Pick<
  AppConfig,
  'theme' | 'brandLogoSrc' | 'features'
>;

/**
 * Type utility for inferring the EDC_UI_PROFILE_DATA keys typed.
 * see https://stackoverflow.com/a/74691877
 *
 * @param profiles Record<EdcUiProfile, EdcUiProfileUtils>
 */
export const inferEdcUiProfileDataKeyTypes = <
  T extends Record<string, EdcUiProfileConfig>,
>(
  profiles: T,
) => profiles;
