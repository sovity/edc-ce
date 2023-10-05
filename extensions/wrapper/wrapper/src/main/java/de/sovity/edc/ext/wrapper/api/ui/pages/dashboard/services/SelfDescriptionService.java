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
import org.eclipse.edc.spi.system.configuration.Config;

import java.util.List;

import static de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.ConfigPropertyUtils.configKey;

@RequiredArgsConstructor
public class SelfDescriptionService {
    private final Config config;
    private static final String PARTICIPANT_ID = configKey("EDC_PARTICIPANT_ID");
    private static final String CONNECTOR_ENDPOINT = configKey("EDC_DSP_CALLBACK_ADDRESS");
    private static final String NAME_KEBAB_CASE = configKey("MY_EDC_NAME_KEBAB_CASE");
    private static final String TITLE = configKey("MY_EDC_TITLE");
    private static final String DESCRIPTION = configKey("MY_EDC_DESCRIPTION");
    private static final String CURATOR_URL = configKey("MY_EDC_CURATOR_URL");
    private static final String CURATOR_NAME = configKey("MY_EDC_CURATOR_NAME");
    private static final String MAINTAINER_URL = configKey("MY_EDC_MAINTAINER_URL");
    private static final String MAINTAINER_NAME = configKey("MY_EDC_MAINTAINER_NAME");
    private static final List<String> REQUIRED = List.of(
            PARTICIPANT_ID,
            CONNECTOR_ENDPOINT,
            NAME_KEBAB_CASE,
            TITLE,
            DESCRIPTION,
            CURATOR_URL,
            CURATOR_NAME,
            MAINTAINER_URL,
            MAINTAINER_NAME
    );

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
                .map(this::configValue)
                .filter(StringUtils::isBlank)
                .toList();
        Validate.isTrue(
                missing.isEmpty(),
                "Missing required configuration properties: %s".formatted(missing)
        );
    }

    String configValue(String configKey) {
        return config.getString(configKey, "");
    }
}
