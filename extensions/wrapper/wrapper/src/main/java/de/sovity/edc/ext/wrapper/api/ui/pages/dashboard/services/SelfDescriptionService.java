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

import de.sovity.edc.utils.config.ConfigProps;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

@RequiredArgsConstructor
public class SelfDescriptionService {
    private final Config config;

    public String getParticipantId() {
        return ConfigProps.EDC_PARTICIPANT_ID.getStringOrNull(config);
    }

    public String getConnectorEndpoint() {
        return ConfigProps.EDC_DSP_CALLBACK_ADDRESS.getStringOrNull(config);
    }

    public String getConnectorTitle() {
        return ConfigProps.MY_EDC_TITLE.getStringOrNull(config);
    }

    public String getConnectorDescription() {
        return ConfigProps.MY_EDC_DESCRIPTION.getStringOrNull(config);
    }

    public String getCuratorUrl() {
        return ConfigProps.MY_EDC_CURATOR_URL.getStringOrNull(config);
    }

    public String getCuratorName() {
        return ConfigProps.MY_EDC_CURATOR_NAME.getStringOrNull(config);
    }

    public String getMaintainerUrl() {
        return ConfigProps.MY_EDC_MAINTAINER_URL.getStringOrNull(config);
    }

    public String getMaintainerName() {
        return ConfigProps.MY_EDC_MAINTAINER_NAME.getStringOrNull(config);
    }
}
