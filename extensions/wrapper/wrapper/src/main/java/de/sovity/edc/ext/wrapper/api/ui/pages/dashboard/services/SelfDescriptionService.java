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

import de.sovity.edc.utils.config.CeConfigProps;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

@RequiredArgsConstructor
public class SelfDescriptionService {
    private final Config config;

    public String getParticipantId() {
        return CeConfigProps.EDC_PARTICIPANT_ID.getStringOrThrow(config);
    }

    public String getConnectorEndpoint() {
        return CeConfigProps.EDC_DSP_CALLBACK_ADDRESS.getStringOrThrow(config);
    }

    public String getConnectorTitle() {
        return CeConfigProps.MY_EDC_TITLE.getStringOrThrow(config);
    }

    public String getConnectorDescription() {
        return CeConfigProps.MY_EDC_DESCRIPTION.getStringOrThrow(config);
    }

    public String getCuratorUrl() {
        return CeConfigProps.MY_EDC_CURATOR_URL.getStringOrThrow(config);
    }

    public String getCuratorName() {
        return CeConfigProps.MY_EDC_CURATOR_NAME.getStringOrThrow(config);
    }

    public String getMaintainerUrl() {
        return CeConfigProps.MY_EDC_MAINTAINER_URL.getStringOrThrow(config);
    }

    public String getMaintainerName() {
        return CeConfigProps.MY_EDC_MAINTAINER_NAME.getStringOrThrow(config);
    }
}
