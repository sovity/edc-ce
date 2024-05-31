package de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigPropertyUtils {

    /**
     * Maps a {@literal CONFIG_KEY} to {@literal config.key}
     *
     * @param envVarKey {@literal CONFIG_KEY}
     * @return {@literal config.key}
     */
    public static String configKey(String envVarKey) {
        return String.join(".", envVarKey.split("_")).toLowerCase();
    }
}
