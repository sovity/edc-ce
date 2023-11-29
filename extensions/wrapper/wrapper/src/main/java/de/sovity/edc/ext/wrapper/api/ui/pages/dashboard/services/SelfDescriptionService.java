/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;

import java.util.List;
import java.util.Objects;

import static de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.ConfigPropertyUtils.configKey;

@RequiredArgsConstructor
public class SelfDescriptionService {
    private final Config config;
    private final Monitor monitor;
    private static final String PARTICIPANT_ID = configKey("MY_EDC_PARTICIPANT_ID");
    private static final String CONNECTOR_ENDPOINT = configKey("EDC_DSP_CALLBACK_ADDRESS");
    private static final String TITLE = configKey("MY_EDC_TITLE");
    private static final String DESCRIPTION = configKey("MY_EDC_DESCRIPTION");
    private static final String CURATOR_URL = configKey("MY_EDC_CURATOR_URL");
    private static final String CURATOR_NAME = configKey("MY_EDC_CURATOR_NAME");
    private static final String MAINTAINER_URL = configKey("MY_EDC_MAINTAINER_URL");
    private static final String MAINTAINER_NAME = configKey("MY_EDC_MAINTAINER_NAME");
    private static final List<String> REQUIRED = List.of(
            PARTICIPANT_ID,
            CONNECTOR_ENDPOINT,
            TITLE,
            DESCRIPTION,
            CURATOR_URL,
            CURATOR_NAME,
            MAINTAINER_URL,
            MAINTAINER_NAME
    );

    /**
     * Eclipse EDC Participant ID. Prefer setting {@link #PARTICIPANT_ID} when configuring the connector.
     */
    private static final String ECLIPSE_EDC_PARTICIPANT_ID = configKey("EDC_PARTICIPANT_ID");

    /**
     * Deprecated Connector ID configuration property.
     * <p>
     * Required for printing out a warning if still set.
     *
     * @deprecated Use {@link #PARTICIPANT_ID} instead.
     */
    @Deprecated(forRemoval = true)
    private static final String NAME_KEBAB_CASE = configKey("MY_EDC_NAME_KEBAB_CASE");

    public String getParticipantId() {
        return configValue(PARTICIPANT_ID);
    }

    public String getConnectorEndpoint() {
        return configValue(CONNECTOR_ENDPOINT);
    }

    public String getConnectorTitle() {
        return configValue(TITLE);
    }

    public String getConnectorDescription() {
        return configValue(DESCRIPTION);
    }

    public String getCuratorUrl() {
        return configValue(CURATOR_URL);
    }

    public String getCuratorName() {
        return configValue(CURATOR_NAME);
    }

    public String getMaintainerUrl() {
        return configValue(MAINTAINER_URL);
    }

    public String getMaintainerName() {
        return configValue(MAINTAINER_NAME);
    }

    public void validateSelfDescriptionConfig() {
        var missing = REQUIRED.stream()
                .filter(key -> StringUtils.isBlank(configValue(key)))
                .toList();
        Validate.isTrue(missing.isEmpty(),
                "Missing required configuration properties: %s".formatted(missing));

        Validate.isTrue(Objects.equals(configValue(PARTICIPANT_ID), configValue(ECLIPSE_EDC_PARTICIPANT_ID)),
                "Config Properties %s and %s have different values. Please set %s instead of %s."
                        .formatted(PARTICIPANT_ID, ECLIPSE_EDC_PARTICIPANT_ID, PARTICIPANT_ID, ECLIPSE_EDC_PARTICIPANT_ID));

        if (StringUtils.isNotBlank(configValue(NAME_KEBAB_CASE))) {
            monitor.warning("Config Property %s is deprecated in favor of %s. Please configure that instead."
                    .formatted(NAME_KEBAB_CASE, PARTICIPANT_ID));
        }
    }

    String configValue(String configKey) {
        return config.getString(configKey, "");
    }
}
