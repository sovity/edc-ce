import {AppConfigProperties} from './app-config-properties';

export class AppConfigMerger {
  /**
   * Merges two configs.
   *
   * @param configName first config name for logging
   * @param config first config
   * @param overridesName second config name for logging
   * @param overrides second config (takes precedence)
   */
  mergeConfigs(
    configName: string,
    config: Record<string, string | null>,
    overridesName: string,
    overrides: Record<string, string | null>,
  ): Record<string, string | null> {
    Object.keys(config)
      .filter((key) => overrides.hasOwnProperty(key))
      .forEach((key) => {
        if (config[key] != overrides[key]) {
          console.info(
            `Overriding '${key}' from '${configName}' with value '${config[key]}'` +
              ` with '${overrides[key]}' from '${overridesName}'.`,
          );
        }
      });

    return {
      ...config,
      ...overrides,
    };
  }

  /**
   * Applies special value {@link AppConfigProperties.configJson} that might contain a JSON as single property.
   *
   * @param configName for better error logging
   * @param config config
   */
  applyEmbeddedConfig(
    configName: string,
    config: Record<string, string | null>,
  ): Record<string, string | null> {
    // Read JSON property
    const embeddedConfigName = `${configName} -> ${AppConfigProperties.configJson}`;
    let embeddedConfig = this.parseEmbeddedConfig(config);

    // Apply Embedded Config recursively
    if (embeddedConfig.hasOwnProperty(AppConfigProperties.configJson)) {
      embeddedConfig = this.applyEmbeddedConfig(
        embeddedConfigName,
        embeddedConfig,
      );
    }

    // Remove Embedded Config key from config for cleanliness
    const {[AppConfigProperties.configJson]: _, ...rest} = config;

    // Merge with original config taking precedence
    config = this.mergeConfigs(
      embeddedConfigName,
      embeddedConfig,
      `${configName}`,
      rest,
    );

    return config;
  }

  /**
   * Tries to parse {@link AppConfigProperties.configJson} as JSON.
   *
   * @param config config
   * @returns parsed JSON
   */
  private parseEmbeddedConfig(
    config: Record<string, string | null>,
  ): Record<string, string | null> {
    try {
      return JSON.parse(config[AppConfigProperties.configJson] || '{}');
    } catch (e) {
      console.error(
        `Could not parse not parse embedded Config JSON`,
        e,
        config[AppConfigProperties.configJson],
      );
      return {};
    }
  }
}
